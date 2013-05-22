package com.tngtech.leapdrone.drone.listeners;

public interface ReadyStateChangeListener
{
  public enum ReadyState
  {
    READY, NOT_READY
  }

  public void onReadyStateChange(ReadyState readyState);
}
