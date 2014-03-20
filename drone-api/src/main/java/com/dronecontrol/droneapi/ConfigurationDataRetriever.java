package com.dronecontrol.droneapi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.dronecontrol.droneapi.components.AddressComponent;
import com.dronecontrol.droneapi.components.ErrorListenerComponent;
import com.dronecontrol.droneapi.components.ReadyStateListenerComponent;
import com.dronecontrol.droneapi.components.TcpComponent;
import com.dronecontrol.droneapi.components.ThreadComponent;
import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.listeners.DroneConfigurationListener;
import com.dronecontrol.droneapi.listeners.ReadyStateChangeListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ConfigurationDataRetriever implements Runnable
{
  public static final String SEPARATOR = " = ";

  private final Logger logger = Logger.getLogger(ConfigurationDataRetriever.class);

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final TcpComponent tcpComponent;

  private final ReadyStateListenerComponent readyStateListenerComponent;

  private final ErrorListenerComponent errorListenerComponent;

  private final Set<DroneConfigurationListener> droneConfigurationListeners;

  private String droneIpAddress;

  private int configDataPort;

  @Inject
  public ConfigurationDataRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, TcpComponent tcpComponent,
                                    ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent)
  {
    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.tcpComponent = tcpComponent;
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;

    droneConfigurationListeners = Sets.newHashSet();
  }

  public void start(String droneIpAddress, int configDataPort)
  {
    this.droneIpAddress = droneIpAddress;
    this.configDataPort = configDataPort;

    logger.info("Starting config data thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping config data thread");
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

  public void addDroneConfigurationListener(DroneConfigurationListener droneConfigurationListener)
  {
    if (!droneConfigurationListeners.contains(droneConfigurationListener))
    {
      droneConfigurationListeners.add(droneConfigurationListener);
    }
  }

  public void removeDroneConfigurationListener(DroneConfigurationListener droneConfigurationListener)
  {
    if (droneConfigurationListeners.contains(droneConfigurationListener))
    {
      droneConfigurationListeners.remove(droneConfigurationListener);
    }
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
    connectToConfigDataPort();
    readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);

    while (!threadComponent.isStopped())
    {
      try
      {
        processData(readLines());
      } catch (Throwable e)
      {
        logger.error("Error processing the config control data", e);
      }
    }

    disconnectFromConfigDataPort();
  }

  private void connectToConfigDataPort()
  {
    logger.info(String.format("Connecting to config data port %d", configDataPort));
    tcpComponent.connect(addressComponent.getInetAddress(droneIpAddress), configDataPort, 1000);
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

    logger.debug("Drone configuration data received");
    DroneConfiguration droneConfiguration = getDroneConfiguration(lines);

    for (DroneConfigurationListener listener : droneConfigurationListeners)
    {
      listener.onDroneConfiguration(droneConfiguration);
    }
  }

  private DroneConfiguration getDroneConfiguration(Collection<String> lines)
  {
    Map<String, String> configMap = Maps.newHashMap();

    for (String line : lines)
    {
      String[] configOption = line.split(SEPARATOR);
      if (configOption.length != 2)
      {
        continue;
      }

      configMap.put(configOption[0], configOption[1]);
    }

    return new DroneConfiguration(configMap);
  }

  private void disconnectFromConfigDataPort()
  {
    logger.info(String.format("Disconnecting from config data port %d", configDataPort));
    tcpComponent.disconnect();
  }
}