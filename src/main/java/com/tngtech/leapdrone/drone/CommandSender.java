package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.WatchDogCommand;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import com.tngtech.leapdrone.helpers.components.UdpComponent;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class CommandSender implements Runnable
{
  private final Logger logger = Logger.getLogger(CommandSender.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final UdpComponent udpComponent;

  private final ReadyStateComponent readyStateComponent;

  private ReadyStateChangeListener.ReadyState readyState = ReadyStateChangeListener.ReadyState.NOT_READY;

  private List<Command> commandsToSend;

  private int sequenceNumber = 1;

  private int sequenceNumberSent = 0;

  @Inject
  public CommandSender(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                       ReadyStateComponent readyStateComponent)
  {
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.udpComponent = udpComponent;
    this.readyStateComponent = readyStateComponent;

    commandsToSend = Lists.newArrayList();
  }

  public void start()
  {
    logger.info("Starting command sender thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping command sender thread");
    threadComponent.stop();
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void sendCommand(Command command)
  {
    queue(command);
  }

  private Command queue(Command command)
  {
    commandsToSend.add(command);
    return command;
  }

  @Override
  public void run()
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
    InetAddress address = addressComponent.getInetAddress(Config.DRONE_IP_ADDRESS);

    logger.info(String.format("Connecting to command send port %d", Config.COMMAND_PORT));
    udpComponent.connect(address, Config.COMMAND_PORT);
  }

  private int sendPendingCommands(int count)
  {
    List<Command> commands = getCommands(count);
    for (Command command : commands)
    {
      send(command);
    }
    sleep(15);
    return count + 1;
  }

  private List<Command> getCommands(int count)
  {
    List<Command> commands = commandsToSend;
    if (count % 10 == 1)
    {
      commands.add(new WatchDogCommand());
    }

    commandsToSend = Lists.newArrayList();
    return commands;
  }

  private void send(Command command)
  {
    if (command.isPreparationCommandNeeded())
    {
      sequenceNumberSent = getSequenceNumber();
      sendCommandText(command.getPreparationCommandText(sequenceNumberSent));
    }

    sequenceNumberSent = getSequenceNumber();
    sendCommandText(command.getCommandText(sequenceNumberSent));
  }

  private void sendCommandText(String commandText)
  {
    byte[] sendData = commandText.getBytes();
    InetAddress address = addressComponent.getInetAddress(Config.DRONE_IP_ADDRESS);
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, Config.COMMAND_PORT);

    if (!commandText.startsWith("AT*COMWDG"))
    {
      System.out.println(commandText);
    }

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
      readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
    }
  }

  private void disconnectFromCommandSenderPort()
  {
    logger.info(String.format("Disconnecting from command send port %d", Config.COMMAND_PORT));
    udpComponent.disconnect();
  }

  public int getSequenceNumberSent()
  {
    return sequenceNumberSent;
  }
}