package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.data.enums.ControllerState;
import com.tngtech.leapdrone.drone.data.enums.DroneVersion;
import com.tngtech.leapdrone.drone.listeners.DroneConfigurationListener;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.helpers.VersionHelper;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import org.apache.log4j.Logger;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;
import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class DroneCoordinator implements ReadyStateChangeListener, NavDataListener, DroneConfigurationListener
{
  private final Logger logger = Logger.getLogger(DroneCoordinator.class.getSimpleName());

  private final AddressComponent addressComponent;

  private final VersionReader versionReader;

  private final CommandSenderCoordinator commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetrieverP264 videoRetrieverP264;

  private final VideoRetrieverH264 videoRetrieverH264;

  private final ConfigurationDataRetriever configurationDataRetriever;

  private Config config;

  private ControllerState currentState;

  private DroneVersion droneVersion;

  private DroneConfiguration droneConfiguration;

  @Inject
  public DroneCoordinator(AddressComponent addressComponent, VersionReader versionReader, CommandSenderCoordinator commandSender,
                          NavigationDataRetriever navigationDataRetriever, VideoRetrieverP264 videoRetrieverP264,
                          VideoRetrieverH264 videoRetrieverH264, ConfigurationDataRetriever configurationDataRetriever)
  {
    this.addressComponent = addressComponent;
    this.versionReader = versionReader;
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetrieverP264 = videoRetrieverP264;
    this.videoRetrieverH264 = videoRetrieverH264;
    this.configurationDataRetriever = configurationDataRetriever;

    addListeners(commandSender);
    currentState = ControllerState.STARTED;
  }

  private void addListeners(CommandSender commandSender)
  {
    commandSender.addReadyStateChangeListener(this);
    configurationDataRetriever.addReadyStateChangeListener(this);
    navigationDataRetriever.addReadyStateChangeListener(this);
    videoRetrieverH264.addReadyStateChangeListener(this);
    videoRetrieverP264.addReadyStateChangeListener(this);

    navigationDataRetriever.addNavDataListener(this);
    configurationDataRetriever.addDroneConfigurationListener(this);
  }

  public void start(Config config)
  {
    this.config = config;
    checkIfDroneIsReachable();
    determineDroneVersion();

    startWorkers();
    waitForState(ControllerState.WORKERS_READY);
    logger.info("Workers are ready to be used");

    login();
    startVideoRetriever();
    determineConfiguration();
    logger.info("Got configuration data");

    checkConfiguration();

    waitForState(ControllerState.READY);
    logger.info("Drone setup complete");
  }

  private void checkIfDroneIsReachable()
  {
    checkState(addressComponent.isReachable(config.getDroneIpAddress(), Config.REACHABLE_TIMEOUT), "The drone could not be pinged");
    logger.info("The drone could be pinged");
  }

  private void determineDroneVersion()
  {
    droneVersion = versionReader.getDroneVersion(config.getDroneIpAddress(), config.getFtpPort());
  }

  private void startWorkers()
  {
    commandSender.start(config.getDroneIpAddress(), config.getCommandPort());
    configurationDataRetriever.start(config.getDroneIpAddress(), config.getConfigDataPort());
    navigationDataRetriever.start(config.getDroneIpAddress(), config.getNavDataPort());
  }

  private void login()
  {
    commandSender.sendLogin(config.getSessionChecksum(), config.getProfileChecksum(), config.getApplicationChecksum());
    commandSender.sendBareConfigCommand(getSetConfigCommand(DroneConfiguration.ENABLE_NAV_DATA_KEY, "TRUE"));
  }

  private void startVideoRetriever()
  {
    if (droneVersion == DroneVersion.AR_DRONE_1)
    {
      commandSender.sendBareConfigCommand(getSetConfigCommand(DroneConfiguration.VIDEO_CODEC_KEY, config.getArDrone1VideoCodec()));
      videoRetrieverP264.start(config.getDroneIpAddress(), config.getVideoDataPort());
    } else
    {
      commandSender.sendBareConfigCommand(getSetConfigCommand(DroneConfiguration.VIDEO_CODEC_KEY, config.getArDrone2VideoCodec()));
      videoRetrieverH264.start(config.getDroneIpAddress(), config.getVideoDataPort());
    }
  }

  private SetConfigValueCommand getSetConfigCommand(String key, Object value)
  {
    return new SetConfigValueCommand(config.getSessionChecksum(), config.getProfileChecksum(), config.getApplicationChecksum(), key, value);
  }

  private void determineConfiguration()
  {
    commandSender.sendRefreshDroneConfigurationCommand();
  }

  private void waitForState(ControllerState state)
  {
    while (currentState != state)
    {
      sleep(Config.WAIT_TIMEOUT);
    }
  }

  private void checkConfiguration()
  {
    String firmwareVersion = droneConfiguration.getConfig().get(DroneConfiguration.FIRMWARE_VERSION_KEY);
    String sessionId = droneConfiguration.getConfig().get(DroneConfiguration.SESSION_ID_KEY);
    String applicationId = droneConfiguration.getConfig().get(DroneConfiguration.APPLICATION_ID_KEY);
    String profileId = droneConfiguration.getConfig().get(DroneConfiguration.PROFILE_ID_KEY);

    checkState(Objects.equals(config.getSessionChecksum(), sessionId), "Session ID checksums do not match");
    checkState(Objects.equals(config.getProfileChecksum(), profileId), "Profile ID checksums do not match");
    checkState(Objects.equals(config.getApplicationChecksum(), applicationId), "Application ID checksums do not match");
    checkState(VersionHelper.compareVersions(firmwareVersion, Config.MIN_FIRMWARE_VERSION) >= 0, "The firmware version used is too old");
  }

  public void stop()
  {
    currentState = ControllerState.STOPPED;

    commandSender.stop();
    navigationDataRetriever.stop();
    configurationDataRetriever.stop();
    stopVideoRetriever();
  }

  private void stopVideoRetriever()
  {
    if (droneVersion == DroneVersion.AR_DRONE_1)
    {
      videoRetrieverP264.stop();
    } else
    {
      videoRetrieverH264.stop();
    }
  }

  @Override
  public void onDroneConfiguration(DroneConfiguration configuration)
  {
    droneConfiguration = configuration;
  }

  @Override
  public void onNavData(NavData navData)
  {
    //noinspection StatementWithEmptyBody
    if (navData.getState().isCommunicationProblemOccurred())
    {
      // TODO if there are problems, reset the command sender sequence number
      // There is no clear reference in the manual for that
      // (@see Developer Manual, page 40 ("How do the client and the drone synchronize?")
    }
  }

  @Override
  public void onReadyStateChange(ReadyState readyState)
  {
    if (readyState == ReadyStateChangeListener.ReadyState.READY)
    {
      currentState = currentState.getNextState();
    }
  }

  public ControllerState getState()
  {
    return currentState;
  }

  public DroneVersion getDroneVersion()
  {
    return droneVersion;
  }

  public DroneConfiguration getDroneConfiguration()
  {
    return droneConfiguration;
  }
}