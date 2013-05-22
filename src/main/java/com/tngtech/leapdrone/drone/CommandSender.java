package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.commands.WatchDogCommand;
import com.tngtech.leapdrone.drone.config.DroneConfig;
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
    sendEnableNavDataCommand();

    while (!threadComponent.isStopped())
    {
      count = sendPendingCommands(count);
      changeReadyState();
    }

    disconnectFromCommandSenderPort();
  }

  private void connectToCommandSenderPort()
  {
    InetAddress address = addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    logger.info(String.format("Connecting to command send port %d", DroneConfig.COMMAND_PORT));
    udpComponent.connect(address, DroneConfig.COMMAND_PORT);
  }

  private void sendEnableNavDataCommand()
  {
    logger.debug("Enabling nav data");
    send(new SetConfigValueCommand("general:navdata_demo", "TRUE"));
  }

  private int sendPendingCommands(int count)
  {
    List<Command> commands = getCommands();
    for (Command command : commands)
    {
      send(command);
    }
    sendWatchDogCommand(count++);
    sleep(15);
    return count;
  }

  private List<Command> getCommands()
  {
    List<Command> commands = commandsToSend;
    commands.add(new WatchDogCommand());

    commandsToSend = Lists.newArrayList();
    return commands;
  }

  private void sendWatchDogCommand(int count)
  {
    if (count % 20 == 0)
    {
      logger.trace("Sending watchdog command");
      send(new WatchDogCommand());
    }
  }

  private void send(Command command)
  {
    sequenceNumberSent = getSequenceNumber();
    byte[] sendData = command.getCommandText(sequenceNumberSent).getBytes();
    InetAddress address = addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DroneConfig.COMMAND_PORT);

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
    logger.info(String.format("Disconnecting from command send port %d", DroneConfig.COMMAND_PORT));
    udpComponent.disconnect();
  }

  public int getSequenceNumberSent()
  {
    return sequenceNumberSent;
  }
}