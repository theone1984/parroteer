package com.tngtech.leapdrone.drone.components;

import static com.google.common.base.Preconditions.checkState;

public class ThreadComponent
{
  private boolean stopped = true;

  public void start(Runnable runnable)
  {
    checkState(stopped, "Already started");
    stopped = false;

    new Thread(runnable).start();
  }

  public void stop()
  {
    checkState(!stopped, "Already stopped");
    stopped = true;
  }

  public boolean isStopped()
  {
    return stopped;
  }
}