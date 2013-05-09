package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

import static com.tngtech.leapdrone.helpers.BinaryDataHelper.getNormalizedIntValue;
import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class CommandSender implements Runnable
{
  private static final int TAKE_OFF_VALUE = 290718208;

  private static final int LAND_VALUE = 290717696;

  private final Logger logger = Logger.getLogger(CommandSender.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final UdpComponent udpComponent;

  private List<String> commandsToSend;

  private int sequenceNumber = 1;

  @Inject
  public CommandSender(ThreadComponent threadComponent, UdpComponent udpComponent)
  {
    this.threadComponent = threadComponent;
    this.udpComponent = udpComponent;

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

  public void sendTakeOffCommand()
  {
    logger.debug("Taking off");
    sendFlightModeCommand(TAKE_OFF_VALUE);
  }

  public void sendLandCommand()
  {
    logger.debug("Landing");
    sendFlightModeCommand(LAND_VALUE);
  }

  private void sendFlightModeCommand(int flightModeValue)
  {
    queue(String.format("AT*REF=%s,%s", sequenceNumber++, flightModeValue));
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    logger.debug(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    queue(String.format("AT*PCMD=%d,%d,%d,%d,%d,%d", sequenceNumber++, 1, getNormalizedIntValue(roll), getNormalizedIntValue(pitch),
            getNormalizedIntValue(gaz), getNormalizedIntValue(yaw)));
  }

  private void queue(String command)
  {
    commandsToSend.add(command);
  }

  @Override
  public void run()
  {
    int count = 1;

    logger.info(String.format("Connecting to command send port %d", DroneConfig.COMMAND_PORT));
    udpComponent.connect(DroneConfig.COMMAND_PORT);
    sendEnableNavDataCommand();

    while (!threadComponent.isStopped())
    {
      List<String> commands = getCommands();
      for (String command : commands)
      {
        send(command);
      }
      sendWatchDogCommand(count++);
      sleep(15);
    }

    logger.info(String.format("Disconnecting from command send port %d", DroneConfig.COMMAND_PORT));
    udpComponent.disconnect();
  }

  private List<String> getCommands()
  {
    List<String> commands = commandsToSend;
    commandsToSend = Lists.newArrayList();
    return commands;
  }

  private void sendEnableNavDataCommand()
  {
    logger.debug("Enabling nav data");
    send(String.format("AT*CONFIG=%s,\"general:navdata_demo\",\"TRUE\"", sequenceNumber++));
  }

  private void sendWatchDogCommand(int count)
  {
    if (count % 20 == 0)
    {
      logger.trace("Sending watchdog command");
      send(String.format("AT*COMWDG=%s", sequenceNumber++));
    }
  }

  private void send(String command)
  {
    command += "\r";
    byte[] sendData = command.getBytes();

    InetAddress address = udpComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DroneConfig.COMMAND_PORT);
    udpComponent.send(sendPacket);
  }
}