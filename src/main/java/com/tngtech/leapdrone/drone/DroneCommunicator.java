package com.tngtech.leapdrone.drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DroneCommunicator
{
  private final static int TAKE_OFF_VALUE = 290718208;

  private static final int LAND_VALUE = 290717696;

  private final InetAddress address;

  private DatagramSocket socket;

  private int sequenceNumber = 1;

  public DroneCommunicator()
  {
    try
    {
      address = InetAddress.getByName("192.168.1.1");
    } catch (UnknownHostException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void connect()
  {
    try
    {
      socket = new DatagramSocket();

    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void sendTakeOff()
  {
    sendFlightModeCommand(TAKE_OFF_VALUE);
  }

  public void sendLand()
  {
    sendFlightModeCommand(LAND_VALUE);
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    String command =
            String.format("AT*PCMD=%d,%d,%d,%d,%d,%d\r", sequenceNumber++, 1, normalizeValue(roll), normalizeValue(pitch), normalizeValue(gaz),
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

  private void sendFlightModeCommand(int flightModeValue)
  {
    String command = String.format("AT*REF=%s,%s\r", sequenceNumber++, flightModeValue);
    send(command);
  }

  public void sendWatchDogCommand()
  {
    String command = String.format("AT*COMWDG=%s\r", sequenceNumber++);
    send(command);
  }

  private void send(String command)
  {
    try
    {
      System.out.println(command);
      byte[] sendData = command.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 5556);
      socket.send(sendPacket);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }

  }
}