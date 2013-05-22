package com.tngtech.leapdrone.helpers.components;

import com.google.common.collect.Sets;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;

import java.util.Set;

public class ReadyStateComponent
{
  private final Set<ReadyStateChangeListener> readyStateChangeListeners;

  public ReadyStateComponent()
  {
    readyStateChangeListeners = Sets.newHashSet();
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    if (!readyStateChangeListeners.contains(readyStateChangeListener))
    {
      readyStateChangeListeners.add(readyStateChangeListener);
    }
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    if (readyStateChangeListeners.contains(readyStateChangeListener))
    {
      readyStateChangeListeners.remove(readyStateChangeListener);
    }
  }

  public void emitReadyStateChange(ReadyStateChangeListener.ReadyState readyState)
  {
    for (ReadyStateChangeListener listener : readyStateChangeListeners)
    {
      listener.onReadyStateChange(readyState);
    }
  }
}