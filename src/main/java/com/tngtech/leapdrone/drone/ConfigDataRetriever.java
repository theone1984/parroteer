package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.TcpComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
    logger.info("Starting config data thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping config data thread");
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
    connectToConfigDataPort();
    readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);

    while (!threadComponent.isStopped())
    {
      try
      {
        processData(readLines());
      } catch (RuntimeException e)
      {
        logger.error("Error processing the config control data", e);
      }
    }

    disconnectFromConfigDataPort();
  }

  private void connectToConfigDataPort()
  {
    logger.info(String.format("Connecting to config data port %d", DroneConfig.CONFIG_DATA_PORT));
    tcpComponent.connect(addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS), DroneConfig.CONFIG_DATA_PORT, 1000);
  }

  public Collection<String> readLines()
  {
    try
    {
      return doReadLines();
    } catch (IOException | ClassNotFoundException e)
    {
      throw new IllegalStateException("Error receiving current lines", e);
    }
  }

  private Collection<String> doReadLines() throws IOException, ClassNotFoundException
  {
    Collection<String> receivedLines = Lists.newArrayList();

    try
    {
      String line = tcpComponent.getReader().readLine();
      while (line != null)
      {
        receivedLines.add(line);
        line = tcpComponent.getReader().readLine();
      }
    } catch (SocketTimeoutException e)
    {
      // EOF is reached (this is a dirty workaround, but there is no indicator telling us when to stop)
    }

    return receivedLines;
  }

  private void processData(Collection<String> lines)
  {
    if (lines.size() == 0)
    {
      return;
    }

    logger.info("Got data");
    System.out.println(lines);
  }

  private void disconnectFromConfigDataPort()
  {
    logger.info(String.format("Connecting to config data port %d", DroneConfig.CONFIG_DATA_PORT));
    tcpComponent.disconnect();
  }
}