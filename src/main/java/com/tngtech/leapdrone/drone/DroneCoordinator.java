package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.ControlDataCommand;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.config.DroneControllerConfig;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
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

  public enum State
  {
    STARTED,
    COMMAND_ONE_RETRIEVER_READY,
    COMMAND_TWO_RETRIEVERS_READY,
    WORKERS_READY,
    READY,
    STOPPED
  }

  private final Logger logger = Logger.getLogger(DroneCoordinator.class.getSimpleName());

  private final AddressComponent addressComponent;

  private final ReadyStateComponent readyStateComponent;

  private final VersionReader versionReader;

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final ConfigurationDataRetriever configurationDataRetriever;

  private final VideoRetrieverAbstract videoRetriever;

  private State currentState;

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
    this.configurationDataRetriever = configurationDataRetriever;
    this.videoRetriever =
            DroneControllerConfig.DRONE_VERSION == DroneControllerConfig.DroneVersion.ARDRONE_1 ? videoRetrieverP264 : videoRetrieverH264;

    addListeners(commandSender);
    currentState = State.STARTED;
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
    videoRetriever.addReadyStateChangeListener(new ReadyStateChangeListener()
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
      currentState = currentState == State.STARTED ? State.COMMAND_ONE_RETRIEVER_READY :
              currentState == State.COMMAND_ONE_RETRIEVER_READY ? State.COMMAND_TWO_RETRIEVERS_READY : State.WORKERS_READY;
    }
  }

  private void videoRetrieverReady(ReadyStateChangeListener.ReadyState readyState)
  {
    if (readyState == ReadyStateChangeListener.ReadyState.READY)
    {
      currentState = State.READY;
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
    waitForState(State.WORKERS_READY);
    logger.info("Workers are ready to be used");

    loginAndDetermineConfiguration();
    logger.info("Got configuration data");

    videoRetriever.start();
    waitForState(State.READY);
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

  private void waitForState(State state)
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
    currentState = State.STOPPED;

    commandSender.stop();
    navigationDataRetriever.stop();
    configurationDataRetriever.stop();
    videoRetriever.stop();
  }

  public State getState()
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

  public VideoRetrieverAbstract getVideoRetriever()
  {
    return videoRetriever;
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