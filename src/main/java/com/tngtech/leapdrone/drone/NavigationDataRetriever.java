package com.tngtech.leapdrone.drone;

import com.tngtech.leapdrone.helpers.BinaryDataHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@SuppressWarnings("InfiniteLoopStatement")
public class NavigationDataRetriever
{

  private static final int NAVDATA_PORT = 5554;

  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  public static final int RECEIVING_BUFFER_SIZE = 10240;

  private static final int NAVDATA_BATTERY_INDEX = 24;

  private static final int NAVDATA_ALTITUDE_INDEX = 40;

  private static final int DATA_LENGTH = 4;

  private final DroneCommandSender commandSender;

  private InetAddress address;

  private DatagramPacket keepAlivePacket;

  private DatagramSocket navigationDataSocket;

  public static void main(String[] args)
  {
    DroneCommandSender commandSender = new DroneCommandSender();
    NavigationDataRetriever navigationDataRetriever = new NavigationDataRetriever(commandSender);

    commandSender.connect();
    navigationDataRetriever.connect();

    navigationDataRetriever.run();
  }

  public NavigationDataRetriever(DroneCommandSender commandSender)
  {
    this.commandSender = commandSender;

    determineDroneAddress();
    determineKeepAlivePacket();
  }

  private void determineDroneAddress()
  {
    try
    {
      address = InetAddress.getByName(DroneController.DRONE_IP_ADDRESS);

    } catch (UnknownHostException e)
    {
      throw new IllegalStateException(e);
    }
  }

  private void determineKeepAlivePacket()
  {
    keepAlivePacket = new DatagramPacket(KEEP_ALIVE_BYTES, KEEP_ALIVE_BYTES.length, address, NAVDATA_PORT);
  }

  private void connect()
  {
    try
    {
      navigationDataSocket = new DatagramSocket(NAVDATA_PORT);
      navigationDataSocket.setSoTimeout(3000);
    } catch (SocketException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void run()
  {
    byte[] receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    DatagramPacket incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);

    sendNavDataReceivingStartCommands();

    while (true)
    {
      try
      {
        receiveData(incomingDataPacket);
        processData(incomingDataPacket, receivingBuffer);

        sendKeepAliveSignal();
      } catch (RuntimeException e)
      {
        e.printStackTrace();
      }
    }
  }

  private void sendNavDataReceivingStartCommands()
  {
    sendKeepAliveSignal();
    commandSender.sendEnableNavDataCommand();
  }

  private void processData(DatagramPacket incomingDataPacket, byte[] receivingBuffer)
  {
    System.out.println("NavData Received: " + incomingDataPacket.getLength() + " bytes");
    System.out.println("Battery: " + BinaryDataHelper.getInt(receivingBuffer, NAVDATA_BATTERY_INDEX, DATA_LENGTH) + "%, Altitude: " +
            ((float) BinaryDataHelper.getInt(receivingBuffer, NAVDATA_ALTITUDE_INDEX, DATA_LENGTH) / 1000) + "m");
  }

  private void sendKeepAliveSignal()
  {
    try
    {
      navigationDataSocket.send(keepAlivePacket);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  private void receiveData(DatagramPacket incomingDataPacket)
  {
    try
    {
      navigationDataSocket.receive(incomingDataPacket);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }
}