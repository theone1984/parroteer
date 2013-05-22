package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.ControlDataCommand;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import org.apache.log4j.Logger;

import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class DroneCoordinator
{

  public static final int WAIT_PERIOD = 15;

  private enum State
  {
    STARTED,
    COMMAND_ONE_RETRIEVER_READY,
    COMMAND_TWO_RETRIEVERS_READY,
    WAITING_FOR_CONTROL_DATA
  }

  private final Logger logger = Logger.getLogger(DroneCoordinator.class.getSimpleName());

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final ConfigDataRetriever configDataRetriever;

  private final VideoRetrieverAbstract videoRetriever;

  private State currentState;

  private NavData currentNavData;

  @Inject
  public DroneCoordinator(CommandSender commandSender, NavigationDataRetriever navigationDataRetriever,
                          ArDroneOneVideoRetriever arDroneOnevideoRetriever, ArDroneTwoVideoRetriever arDroneTwoVideoRetriever,
                          ConfigDataRetriever configDataRetriever)
  {
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.configDataRetriever = configDataRetriever;
    this.videoRetriever = DroneConfig.DRONE_VERSION == DroneConfig.DroneVersion.ARDRONE_1 ? arDroneOnevideoRetriever : arDroneTwoVideoRetriever;

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
    configDataRetriever.addReadyStateChangeListener(new ReadyStateChangeListener()
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

    navigationDataRetriever.addNavDataListener(new NavDataListener()
    {
      @Override
      public void onNavData(NavData navData)
      {
        navDataReceived(navData);
      }
    });
  }

  private void workerReady(ReadyStateChangeListener.ReadyState readyState)
  {
    if (readyState == ReadyStateChangeListener.ReadyState.READY)
    {
      currentState = currentState == State.STARTED ? State.COMMAND_ONE_RETRIEVER_READY :
              currentState == State.COMMAND_ONE_RETRIEVER_READY ? State.COMMAND_TWO_RETRIEVERS_READY : State.WAITING_FOR_CONTROL_DATA;
    }
  }

  private void navDataReceived(NavData navData)
  {
    currentNavData = navData;
  }

  public void start()
  {
    commandSender.start();
    configDataRetriever.start();
    navigationDataRetriever.start();

    waitForState(State.WAITING_FOR_CONTROL_DATA);
    logger.info("Command sender, control and nav data retriever are ready to be used");

    sendConfigCommandToBeAcknowledged(new ControlDataCommand(ControlDataCommand.ControlDataMode.GET_CONTROL_DATA));

    System.out.println("Done!");
  }

  private void sendConfigCommandToBeAcknowledged(Command configCommand)
  {
    waitForCommandAcknowledgeFlagToBe(true);
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

  public void stop()
  {
    commandSender.stop();
    navigationDataRetriever.stop();
    configDataRetriever.stop();
    videoRetriever.stop();
  }
}