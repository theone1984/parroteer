package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.ControlDataCommand;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.config.DroneControllerConfig;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.DroneControllerState;
import com.tngtech.leapdrone.drone.data.DroneVersion;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.DroneConfigurationListener;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;
import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class DroneCoordinator
{

  public static final int WAIT_PERIOD = 15;

  public VideoRetrieverP264 getVideoRetrieverP264()
  {
    return videoRetrieverP264;
  }

  public VideoRetrieverH264 getVideoRetrieverH264()
  {
    return videoRetrieverH264;
  }


  private final Logger logger = Logger.getLogger(DroneCoordinator.class.getSimpleName());

  private final AddressComponent addressComponent;

  private final ReadyStateComponent readyStateComponent;

  private final VersionReader versionReader;

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetrieverP264 videoRetrieverP264;

  private final VideoRetrieverH264 videoRetrieverH264;

  private final ConfigurationDataRetriever configurationDataRetriever;

  private DroneControllerState currentState;

  private DroneVersion droneVersion;

  private DroneConfiguration droneConfiguration;

  private NavData currentNavData;

  @Inject
  public DroneCoordinator(AddressComponent addressComponent, ReadyStateComponent readyStateComponent, VersionReader versionReader,
                          CommandSender commandSender, NavigationDataRetriever navigationDataRetriever,
                          VideoRetrieverP264 videoRetrieverP264, VideoRetrieverH264 videoRetrieverH264,
                          ConfigurationDataRetriever configurationDataRetriever)
  {
    this.addressComponent = addressComponent;
    this.readyStateComponent = readyStateComponent;
    this.versionReader = versionReader;
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetrieverP264 = videoRetrieverP264;
    this.videoRetrieverH264 = videoRetrieverH264;
    this.configurationDataRetriever = configurationDataRetriever;

    addListeners(commandSender);
    currentState = DroneControllerState.STARTED;
  }

  private void addListeners(CommandSender commandSender)
  {
    commandSender.addReadyStateChangeListener(new ReadyStateChangeListener()
    {
      @Override
      public void onReadyStateChange(ReadyState readyState)
      {
        workerReady(readyState);
      }
    });
    configurationDataRetriever.addReadyStateChangeListener(new ReadyStateChangeListener()
    {
      @Override
      public void onReadyStateChange(ReadyState readyState)
      {
        workerReady(readyState);
      }
    });
    navigationDataRetriever.addReadyStateChangeListener(new ReadyStateChangeListener()
    {
      @Override
      public void onReadyStateChange(ReadyState readyState)
      {
        workerReady(readyState);
      }
    });
    videoRetrieverH264.addReadyStateChangeListener(new ReadyStateChangeListener()
    {
      @Override
      public void onReadyStateChange(ReadyState readyState)
      {
        videoRetrieverReady(readyState);
      }
    });
    videoRetrieverP264.addReadyStateChangeListener(new ReadyStateChangeListener()
    {
      @Override
      public void onReadyStateChange(ReadyState readyState)
      {
        videoRetrieverReady(readyState);
      }
    });

    navigationDataRetriever.addNavDataListener(new NavDataListener()
    {
      @Override
      public void onNavData(NavData navData)
      {
        navDataReceived(navData);
      }
    });
    configurationDataRetriever.addDroneConfigurationListener(new DroneConfigurationListener()
    {
      @Override
      public void onDroneConfiguration(DroneConfiguration config)
      {
        droneConfigurationReceived(config);
      }
    });
  }

  private void workerReady(ReadyStateChangeListener.ReadyState readyState)
  {
    if (readyState == ReadyStateChangeListener.ReadyState.READY)
    {
      currentState = currentState == DroneControllerState.STARTED ? DroneControllerState.COMMAND_ONE_RETRIEVER_READY :
              currentState == DroneControllerState.COMMAND_ONE_RETRIEVER_READY ? DroneControllerState.COMMAND_TWO_RETRIEVERS_READY :
                      DroneControllerState.WORKERS_READY;
    }
  }

  private void videoRetrieverReady(ReadyStateChangeListener.ReadyState readyState)
  {
    if (readyState == ReadyStateChangeListener.ReadyState.READY)
    {
      currentState = DroneControllerState.READY;
    }
  }

  private void droneConfigurationReceived(DroneConfiguration config)
  {
    droneConfiguration = config;
  }

  private void navDataReceived(NavData navData)
  {
    currentNavData = navData;
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void start()
  {
    checkIfDroneIsReachable();
    droneVersion = versionReader.getDroneVersion();

    commandSender.start();
    configurationDataRetriever.start();
    navigationDataRetriever.start();
    waitForState(DroneControllerState.WORKERS_READY);
    logger.info("Workers are ready to be used");

    loginAndDetermineConfiguration();
    logger.info("Got configuration data");

    startVideoRetriever();
    waitForState(DroneControllerState.READY);
    logger.info("Drone setup complete");

    readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
  }

  private void checkIfDroneIsReachable()
  {
    checkState(addressComponent.isReachable(DroneControllerConfig.DRONE_IP_ADDRESS, DroneControllerConfig.REACHABLE_TIMEOUT),
            "The drone could not be pinged");
    logger.info("The drone could be pinged");
  }

  private void loginAndDetermineConfiguration()
  {
    sendConfigCommandToBeAcknowledged(new SetConfigValueCommand(DroneConfiguration.SESSION_ID_KEY, DroneControllerConfig.SESSION_ID));
    sendConfigCommandToBeAcknowledged(new SetConfigValueCommand(DroneConfiguration.PROFILE_ID_KEY, DroneControllerConfig.PROFILE_ID));
    sendConfigCommandToBeAcknowledged(new SetConfigValueCommand(DroneConfiguration.APPLICATION_ID_KEY, DroneControllerConfig.APPLICATION_ID));

    sendConfigCommandToBeAcknowledged(new SetConfigValueCommand("general:navdata_demo", "TRUE"));

    sendConfigCommandToBeAcknowledged(new ControlDataCommand(ControlDataCommand.ControlDataMode.GET_CONTROL_DATA));
    waitForConfigurationData();
  }

  private void startVideoRetriever()
  {
    if (droneVersion == DroneVersion.AR_DRONE_1)
    {
      videoRetrieverP264.start();
    } else
    {
      videoRetrieverH264.start();
    }
  }

  private void sendConfigCommandToBeAcknowledged(Command configCommand)
  {
    sendResetControlDataAcknowledgementFlagCommand();
    waitForCommandAcknowledgeFlagToBe(false);

    commandSender.sendCommand(configCommand);
    waitForCommandAcknowledgeFlagToBe(true);
  }

  private void sendResetControlDataAcknowledgementFlagCommand()
  {
    commandSender.sendCommand(new ControlDataCommand(ControlDataCommand.ControlDataMode.RESET_ACK_FLAG));
  }

  private void waitForState(DroneControllerState state)
  {
    while (currentState != state)
    {
      sleep(WAIT_PERIOD);
    }
  }

  private void waitForCommandAcknowledgeFlagToBe(boolean value)
  {
    while (currentNavData == null || currentNavData.getState().isControlReceived() != value)
    {
      sleep(WAIT_PERIOD);
    }
  }

  private void waitForConfigurationData()
  {
    while (droneConfiguration == null)
    {
      sleep(WAIT_PERIOD);
    }
  }

  public void stop()
  {
    currentState = DroneControllerState.STOPPED;

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

  public DroneControllerState getState()
  {
    return currentState;
  }

  public NavigationDataRetriever getNavigationDataRetriever()
  {
    return navigationDataRetriever;
  }

  public CommandSender getCommandSender()
  {
    return commandSender;
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