package com.dronecontrol.droneapi.components;

import static com.google.common.base.Preconditions.checkState;

public class ThreadComponent
{
  private boolean stopped = true;

  private Thread currentThread;

  public void start(Runnable runnable)
  {
    checkState(stopped, "Already started");
    stopped = false;

    currentThread = new Thread(runnable);
    currentThread.start();
  }

  public void stop()
  {
    stopped = true;
  }

  public void stopAndWait()
  {
    try
    {
      stop();
      if (currentThread != null)
      {
        currentThread.join();
      }
    } catch (InterruptedException e)
    {
      throw new IllegalStateException("Joining threads was interrupted", e);
    }
  }

  public boolean isStopped()
  {
    return stopped;
  }
}