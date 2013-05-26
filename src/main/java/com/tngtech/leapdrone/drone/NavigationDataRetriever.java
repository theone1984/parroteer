package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.drone.components.ReadyStateListenerComponent;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.navdata.NavigationDataDecoder;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Set;

import static com.tngtech.leapdrone.drone.helpers.ThreadHelper.sleep;

public class NavigationDataRetriever implements Runnable
{
  public static final int RECEIVING_BUFFER_SIZE = 10240;

  private final Logger logger = Logger.getLogger(NavigationDataRetriever.class);

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final UdpComponent udpComponent;

  private final ReadyStateListenerComponent readyStateListenerComponent;

  private final ErrorListenerComponent errorListenerComponent;

  private final NavigationDataDecoder decoder;

  private final Set<NavDataListener> navDataListeners;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  private String droneIpAddress;

  private int navDataPort;

  @Inject
  public NavigationDataRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                                 ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                                 NavigationDataDecoder decoder)
  {
    super();
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.udpComponent = udpComponent;
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;
    this.decoder = decoder;
    navDataListeners = Sets.newLinkedHashSet();

    determineDatagramPackets();
  }

  public void start(String droneIpAddress, int navDataPort)
  {
    this.droneIpAddress = droneIpAddress;
    this.navDataPort = navDataPort;

    logger.info("Starting nav data thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping nav data thread");
    threadComponent.stopAndWait();
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
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
    try
    {
      doRun();
    } catch (Throwable e)
    {
      errorListenerComponent.emitError(e);
    }
  }

  private void doRun()
  {
    connectToNavDataPort();
    initializeCommunication();
    readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);

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
    InetAddress address = addressComponent.getInetAddress(droneIpAddress);

    logger.info(String.format("Connecting to nav data port %d", navDataPort));
    udpComponent.connect(address, navDataPort);
  }

  private void initializeCommunication()
  {
    sleep(100);
    udpComponent.sendKeepAlivePacket();
  }

  private void processData()
  {
    NavData navData = getNavData();
    if (navData == null)
    {
      return;
    }
    logger.trace(String.format("Received nav data - battery level: %d percent, altitude: %.2f", navData.getBatteryLevel(), navData.getAltitude()));
    for (NavDataListener listener : navDataListeners)
    {
      listener.onNavData(navData);
    }
  }

  private NavData getNavData()
  {
    try
    {
      return decoder.getNavDataFrom(receivingBuffer, incomingDataPacket.getLength());
    } catch (RuntimeException e)
    {
      // Happens from time to time
      return null;
    }
  }

  private void disconnectFromNavDataPort()
  {
    logger.info(String.format("Disconnecting from nav data port %d", navDataPort));
    udpComponent.disconnect();
  }
}