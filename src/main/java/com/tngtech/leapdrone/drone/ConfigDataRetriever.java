package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.TcpComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import org.apache.log4j.Logger;

import java.util.Collection;

public class ConfigDataRetriever implements Runnable
{
  private final Logger logger = Logger.getLogger(ConfigDataRetriever.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final TcpComponent tcpComponent;

  private final ReadyStateComponent readyStateComponent;

  @Inject
  public ConfigDataRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, TcpComponent tcpComponent,
                             ReadyStateComponent readyStateComponent)
  {
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.tcpComponent = tcpComponent;
    this.readyStateComponent = readyStateComponent;
  }

  public void start()
  {
    logger.info("Starting control data thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping control data thread");
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

  @Override
  public void run()
  {
    connectToControlDataPort();
    readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);

    while (!threadComponent.isStopped())
    {
      try
      {
        processData(tcpComponent.readLines());
      } catch (RuntimeException e)
      {
        logger.error("Error processing the drone control data", e);
      }
    }

    disconnectFromControlDataPort();
  }

  private void connectToControlDataPort()
  {
    logger.info(String.format("Connecting to control data port %d", DroneConfig.CONTROL_DATA_PORT));
    tcpComponent.connect(addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS), DroneConfig.CONTROL_DATA_PORT);
  }

  private void processData(Collection<String> lines)
  {
    logger.info("Got data");
    System.out.println(lines);
  }

  private void disconnectFromControlDataPort()
  {
    logger.info(String.format("Connecting to control data port %d", DroneConfig.CONTROL_DATA_PORT));
    tcpComponent.disconnect();
  }
}