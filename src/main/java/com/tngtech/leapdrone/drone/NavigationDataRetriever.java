package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.helpers.BinaryDataHelper;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class NavigationDataRetriever implements Runnable
{
  private static final int NAVDATA_PORT = 5554;

  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  public static final int RECEIVING_BUFFER_SIZE = 10240;

  private static final int NAVDATA_BATTERY_INDEX = 24;

  private static final int NAVDATA_ALTITUDE_INDEX = 40;

  private static final int DATA_LENGTH = 4;

  private final ThreadComponent threadComponent;

  private final UdpComponent udpComponent;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  private DatagramPacket keepAlivePacket;

  @Inject
  public NavigationDataRetriever(ThreadComponent threadComponent, UdpComponent udpComponent)
  {
    super();
    this.threadComponent = threadComponent;
    this.udpComponent = udpComponent;

    determineDatagramPackets();
  }

  public void start()
  {
    threadComponent.start(this);
  }

  public void stop()
  {
    threadComponent.stop();
  }

  private void determineDatagramPackets()
  {
    InetAddress address = udpComponent.getInetAddress(DroneController.DRONE_IP_ADDRESS);

    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
    keepAlivePacket = new DatagramPacket(KEEP_ALIVE_BYTES, KEEP_ALIVE_BYTES.length, address, NAVDATA_PORT);
  }

  @Override
  public void run()
  {
    udpComponent.connect(NAVDATA_PORT);
    udpComponent.send(keepAlivePacket);

    while (!threadComponent.isStopped())
    {
      try
      {
        udpComponent.receive(incomingDataPacket);
        processData();

        udpComponent.send(keepAlivePacket);
      } catch (RuntimeException e)
      {
        e.printStackTrace();
      }
    }
    udpComponent.disconnect();
  }

  private void processData()
  {
    //System.out.println("NavData Received: " + incomingDataPacket.getLength() + " bytes");
    System.out.println("Battery: " + BinaryDataHelper.getInt(receivingBuffer, NAVDATA_BATTERY_INDEX, DATA_LENGTH) + "%, Altitude: " +
            ((float) BinaryDataHelper.getInt(receivingBuffer, NAVDATA_ALTITUDE_INDEX, DATA_LENGTH) / 1000) + "m");
  }
}