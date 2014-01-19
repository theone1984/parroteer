package com.dronecontrol.kinectcontrol.input;

import com.dronecontrol.kinectcontrol.input.data.MovementData;
import com.dronecontrol.kinectcontrol.input.events.MovementDataListener;
import com.dronecontrol.kinectcontrol.input.socket.SocketClientDataListener;
import com.google.common.collect.Sets;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KinectDataReceiver implements SocketClientDataListener
{
  private final int FAILSAFE_DELAY = 200;

  private final ScheduledExecutorService worker;

  private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(KinectDataReceiver.class);

  private final Runnable movementValidityChecker = new Runnable()
  {
    @Override
    public void run()
    {
      checkDelayForLastCommand();
    }
  };

  private final ObjectMapper objectMapper;

  private Set<MovementDataListener> movementDataListeners;

  private long lastCommandTimeStamp;

  @Inject
  public KinectDataReceiver(ObjectMapper objectMapper)
  {
    this.objectMapper = objectMapper;

    movementDataListeners = Sets.newCopyOnWriteArraySet();
    worker = Executors.newSingleThreadScheduledExecutor();
    lastCommandTimeStamp = getCurrentTimeStamp();

    startCheckingMovementValidity();
  }

  public void startCheckingMovementValidity()
  {
    worker.scheduleAtFixedRate(movementValidityChecker, FAILSAFE_DELAY, FAILSAFE_DELAY, TimeUnit.MILLISECONDS);
  }

  public void dispose()
  {
    worker.shutdown();
  }

  private void checkDelayForLastCommand()
  {
    if (timeSinceLastCommand() > FAILSAFE_DELAY)
    {
      emitMovementData(MovementData.NO_MOVEMENT);
    }
  }

  private long timeSinceLastCommand()
  {
    return getCurrentTimeStamp() - lastCommandTimeStamp;
  }

  private long getCurrentTimeStamp()
  {
    return new java.util.Date().getTime();
  }

  @Override
  public void OnData(String message)
  {
    MovementData movementData = getMovementData(message);
    if (movementData != null)
    {
      emitMovementData(movementData);
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

  private void emitMovementData(MovementData movementData)
  {
    lastCommandTimeStamp = getCurrentTimeStamp();
    invokeMovementDataListeners(movementData);
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