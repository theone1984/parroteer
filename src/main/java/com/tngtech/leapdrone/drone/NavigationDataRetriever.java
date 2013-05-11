package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.helpers.BinaryDataHelper;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Set;

import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class NavigationDataRetriever implements Runnable
{
  public static final int RECEIVING_BUFFER_SIZE = 10240;

  private static final int NAVDATA_BATTERY_INDEX = 24;

  private static final int NAVDATA_ALTITUDE_INDEX = 40;

  private static final int DATA_LENGTH = 4;

  private final Logger logger = Logger.getLogger(NavigationDataRetriever.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final UdpComponent udpComponent;

  private final Set<NavDataListener> navDataListeners;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  @Inject
  public NavigationDataRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent)
  {
    super();
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.udpComponent = udpComponent;
    navDataListeners = Sets.newLinkedHashSet();

    determineDatagramPackets();
  }

  public void start()
  {
    logger.info("Starting nav data thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping nav data thread");
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
    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
  }

  @Override
  public void run()
  {
    connectToNavDataPort();
    initializeCommunication();

    while (!threadComponent.isStopped())
    {
      try
      {
        udpComponent.receive(incomingDataPacket);
        processData();

        udpComponent.sendKeepAlivePacket();
      } catch (RuntimeException e)
      {
        udpComponent.sendKeepAlivePacket();
        logger.error(e.getMessage(), e);
      }
    }

    disconnectFromNavDataPort();
  }

  private void connectToNavDataPort()
  {
    InetAddress address = addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    logger.info(String.format("Connecting to nav data port %d", DroneConfig.NAVDATA_PORT));
    udpComponent.connect(address, DroneConfig.NAVDATA_PORT);
  }

  private void initializeCommunication()
  {
    sleep(100);
    udpComponent.sendKeepAlivePacket();
  }

  private void processData()
  {
    NavData navData = getNavData();
    logger.trace(String.format("Received nav data - battery level: %d percent, altitude: %.2f", navData.getBatteryLevel(), navData.getAltitude()));

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

  private void disconnectFromNavDataPort()
  {
    logger.info(String.format("Disconnecting from nav data port %d", DroneConfig.NAVDATA_PORT));
    udpComponent.disconnect();
  }
}