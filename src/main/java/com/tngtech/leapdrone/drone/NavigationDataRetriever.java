package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.helpers.BinaryDataHelper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Set;

public class NavigationDataRetriever implements Runnable
{
  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  public static final int RECEIVING_BUFFER_SIZE = 10240;

  private static final int NAVDATA_BATTERY_INDEX = 24;

  private static final int NAVDATA_ALTITUDE_INDEX = 40;

  private static final int DATA_LENGTH = 4;

  private final ThreadComponent threadComponent;

  private final UdpComponent udpComponent;

  private final Set<NavDataListener> navDataListeners;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  private DatagramPacket keepAlivePacket;

  @Inject
  public NavigationDataRetriever(ThreadComponent threadComponent, UdpComponent udpComponent)
  {
    super();
    this.threadComponent = threadComponent;
    this.udpComponent = udpComponent;
    navDataListeners = Sets.newLinkedHashSet();

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

  public void addNavDataListener(NavDataListener navDataListener)
  {
    if (!navDataListeners.contains(navDataListener))
    {
      navDataListeners.add(navDataListener);
    }
  }

  public void removeNavDataListener(NavDataListener navDataListener)
  {
    if (navDataListeners.contains(navDataListener))
    {
      navDataListeners.remove(navDataListener);
    }
  }

  private void determineDatagramPackets()
  {
    InetAddress address = udpComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
    keepAlivePacket = new DatagramPacket(KEEP_ALIVE_BYTES, KEEP_ALIVE_BYTES.length, address, DroneConfig.NAVDATA_PORT);
  }

  @Override
  public void run()
  {
    udpComponent.connect(DroneConfig.NAVDATA_PORT);
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
    NavData navData = getNavData();

    for (NavDataListener listener : navDataListeners)
    {
      listener.onNavData(navData);
    }
  }

  public NavData getNavData()
  {
    int batteryLevel = BinaryDataHelper.getIntValue(receivingBuffer, NAVDATA_BATTERY_INDEX, DATA_LENGTH);
    float altitude = (float) BinaryDataHelper.getIntValue(receivingBuffer, NAVDATA_ALTITUDE_INDEX, DATA_LENGTH) / 1000;

    return new NavData(batteryLevel, altitude);
  }
}