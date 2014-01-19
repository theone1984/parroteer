package com.dronecontrol.kinectcontrol.input;

import com.dronecontrol.kinectcontrol.input.data.MovementData;
import com.dronecontrol.kinectcontrol.input.events.MovementDataListener;
import com.dronecontrol.kinectcontrol.input.socket.SocketClientDataListener;
import com.google.common.collect.Sets;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

public class KinectDataReceiver implements SocketClientDataListener
{
  private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(KinectDataReceiver.class);

  private final ObjectMapper objectMapper;

  private Set<MovementDataListener> movementDataListeners;

  @Inject
  public KinectDataReceiver(ObjectMapper objectMapper)
  {
    this.objectMapper = objectMapper;

    movementDataListeners = Sets.newCopyOnWriteArraySet();
  }

  @Override
  public void OnData(String message)
  {
    MovementData movementData = getMovementData(message);

    if (movementData != null)
    {
      invokeMovementDataListeners(movementData);
    }
  }

  private MovementData getMovementData(String message)
  {
    try
    {
      return objectMapper.readValue(message, MovementData.class);
    } catch (IOException e)
    {
      logger.warn(String.format("Error while deserializing movement data '%s': %s", message, e.getMessage()));
      return null;
    }
  }

  private void invokeMovementDataListeners(MovementData movementData)
  {
    for (MovementDataListener listener : movementDataListeners)
    {
      listener.onMovementData(movementData);
    }
  }

  public synchronized void addMovementDataListener(MovementDataListener listener)
  {
    movementDataListeners.add(listener);
  }
}