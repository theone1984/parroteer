package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

import static com.tngtech.leapdrone.helpers.BinaryDataHelper.getNormalizedIntValue;
import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class CommandSender implements Runnable
{
  private static final int TAKE_OFF_VALUE = 290718208;

  private static final int LAND_VALUE = 290717696;

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
    threadComponent.start(this);
  }

  public void stop()
  {
    threadComponent.stop();
  }

  public void sendTakeOffCommand()
  {
    sendFlightModeCommand(TAKE_OFF_VALUE);
  }

  public void sendLandCommand()
  {
    sendFlightModeCommand(LAND_VALUE);
  }

  private void sendFlightModeCommand(int flightModeValue)
  {
    queue(String.format("AT*REF=%s,%s", sequenceNumber++, flightModeValue));
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
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
    send(String.format("AT*CONFIG=%s,\"general:navdata_demo\",\"TRUE\"", sequenceNumber++));
  }

  private void sendWatchDogCommand(int count)
  {
    if (count % 20 == 0)
    {
      send(String.format("AT*COMWDG=%s", sequenceNumber++));
    }
  }

  private void send(String command)
  {
    System.out.println(command);

    command += "\r";
    byte[] sendData = command.getBytes();

    InetAddress address = udpComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DroneConfig.COMMAND_PORT);
    udpComponent.send(sendPacket);
  }
}