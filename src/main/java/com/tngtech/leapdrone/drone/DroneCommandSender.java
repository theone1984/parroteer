package com.tngtech.leapdrone.drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DroneCommandSender
{
  public static final int DRONE_COMMAND_PORT = 5556;

  private static final int TAKE_OFF_VALUE = 290718208;

  private static final int LAND_VALUE = 290717696;

  private final InetAddress address;

  private DatagramSocket commandSenderSocket;

  private int sequenceNumber = 1;

  public DroneCommandSender()
  {
    try
    {
      address = InetAddress.getByName(DroneController.DRONE_IP_ADDRESS);
    } catch (UnknownHostException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void connect()
  {
    try
    {
      commandSenderSocket = new DatagramSocket();
      commandSenderSocket.setSoTimeout(3000);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void sendEnableNavDataCommand()
  {
    String command = String.format("AT*CONFIG=%s,\"general:navdata_demo\",\"TRUE\"", sequenceNumber++);
    send(command);
  }

  public void sendWatchDogCommand()
  {
    String command = String.format("AT*COMWDG=%s", sequenceNumber++);
    send(command);
  }

  public void sendTakeOff()
  {
    sendFlightModeCommand(TAKE_OFF_VALUE);
  }

  public void sendLand()
  {
    sendFlightModeCommand(LAND_VALUE);
  }

  private void sendFlightModeCommand(int flightModeValue)
  {
    String command = String.format("AT*REF=%s,%s", sequenceNumber++, flightModeValue);
    send(command);
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    String command =
            String.format("AT*PCMD=%d,%d,%d,%d,%d,%d", sequenceNumber++, 1, normalizeValue(roll), normalizeValue(pitch), normalizeValue(gaz),
                    normalizeValue(yaw));
    send(command);
  }

  protected int normalizeValue(Float value)
  {
    if (value < -1.0f)
    {
      value = -1.0f;
    } else if (value > 1.0f)
    {
      value = 1.0f;
    }

    return Float.floatToIntBits(value);
  }

  private void send(String command)
  {
    try
    {
      command += "\r";
      byte[] sendData = command.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, DRONE_COMMAND_PORT);
      commandSenderSocket.send(sendPacket);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }
}