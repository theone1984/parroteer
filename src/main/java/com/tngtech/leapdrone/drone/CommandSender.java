package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.ATCommand;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.simple.WatchDogCommand;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.drone.components.ReadyStateListenerComponent;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

import static com.tngtech.leapdrone.drone.helpers.ThreadHelper.sleep;

public class CommandSender implements Runnable
{
  private final Logger logger = Logger.getLogger(CommandSender.class);

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final UdpComponent udpComponent;

  private final ReadyStateListenerComponent readyStateListenerComponent;

  private final ErrorListenerComponent errorListenerComponent;

  private final InternalStateWatcher internalStateWatcher;

  private ReadyStateChangeListener.ReadyState readyState = ReadyStateChangeListener.ReadyState.NOT_READY;

  private List<ATCommand> commandsToSend;

  private int sequenceNumber = 1;

  private String droneIpAddress;

  private int commandPort;

  @Inject
  public CommandSender(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                       ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                       InternalStateWatcher internalStateWatcher)
  {
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.udpComponent = udpComponent;
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;
    this.internalStateWatcher = internalStateWatcher;

    commandsToSend = Lists.newArrayList();
  }

  public void start(String droneIpAddress, int commandPort)
  {
    this.droneIpAddress = droneIpAddress;
    this.commandPort = commandPort;

    logger.info("Starting command sender thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping command sender thread");
    threadComponent.stopAndWait();
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void sendCommand(ATCommand command)
  {
    queue(command);
  }

  private Command queue(ATCommand command)
  {
    commandsToSend.add(command);
    return command;
  }

  @Override
  public void run()
  {
    try
    {
      doRun();
    } catch (Throwable e)
    {
      errorListenerComponent.emitError(e);
    }
  }

  private void doRun()
  {
    int count = 1;
    connectToCommandSenderPort();

    while (!threadComponent.isStopped())
    {
      count = sendPendingCommands(count);
      changeReadyState();
    }

    disconnectFromCommandSenderPort();
  }

  private void connectToCommandSenderPort()
  {
    InetAddress address = addressComponent.getInetAddress(droneIpAddress);

    logger.info(String.format("Connecting to command send port %d", commandPort));
    udpComponent.connect(address, commandPort);
  }

  private int sendPendingCommands(int count)
  {
    List<ATCommand> commands = getCommands(count);
    for (ATCommand command : commands)
    {
      send(command);
    }
    sleep(15);
    return count + 1;
  }

  private List<ATCommand> getCommands(int count)
  {
    List<ATCommand> commands = commandsToSend;
    if (count % 10 == 1)
    {
      commands.add(new WatchDogCommand());
    }
    commands.addAll(internalStateWatcher.getCommandsToUpholdInternalState());

    commandsToSend = Lists.newArrayList();
    return commands;
  }

  private void send(ATCommand command)
  {
    if (command.isPreparationCommandNeeded())
    {
      sendCommandText(command.getPreparationCommandText(getSequenceNumber()));
    }

    sendCommandText(command.getCommandText(getSequenceNumber()));
  }

  private void sendCommandText(String commandText)
  {
    byte[] sendData = commandText.getBytes();
    InetAddress address = addressComponent.getInetAddress(droneIpAddress);
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, commandPort);

    // For debugging purposes ... used very often
    /*if (!commandText.startsWith("AT*COMWDG"))
    {
      System.out.println(commandText);
    }*/

    udpComponent.send(sendPacket);
  }

  private int getSequenceNumber()
  {
    return sequenceNumber++;
  }

  private void changeReadyState()
  {
    if (readyState != ReadyStateChangeListener.ReadyState.READY)
    {
      readyState = ReadyStateChangeListener.ReadyState.READY;
      readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
    }
  }

  private void disconnectFromCommandSenderPort()
  {
    logger.info(String.format("Disconnecting from command send port %d", commandPort));
    udpComponent.disconnect();
  }
}