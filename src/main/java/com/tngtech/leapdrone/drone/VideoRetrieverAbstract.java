package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.drone.components.ReadyStateListenerComponent;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.Set;

public abstract class VideoRetrieverAbstract implements Runnable
{
  private final Logger logger = Logger.getLogger(VideoRetrieverAbstract.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final ReadyStateListenerComponent readyStateListenerComponent;

  private final ErrorListenerComponent errorListenerComponent;

  private final Set<VideoDataListener> videoDataListeners;

  private InetAddress droneAddress;

  private int videoDataPort;

  @Inject
  public VideoRetrieverAbstract(ThreadComponent threadComponent, AddressComponent addressComponent,
                                ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent)
  {
    super();

    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;

    videoDataListeners = Sets.newLinkedHashSet();
  }

  public void start(String droneIpAddress, int videoDataPort)
  {
    droneAddress = addressComponent.getInetAddress(droneIpAddress);
    this.videoDataPort = videoDataPort;

    logger.info("Starting video thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping video thread");
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

  public void addVideoDataListener(VideoDataListener videoDataListener)
  {
    if (!videoDataListeners.contains(videoDataListener))
    {
      videoDataListeners.add(videoDataListener);
    }
  }

  public void removeVideoDataListener(VideoDataListener videoDataListener)
  {
    if (videoDataListeners.contains(videoDataListener))
    {
      videoDataListeners.remove(videoDataListener);
    }
  }

  protected Set<VideoDataListener> getVideoDataListeners()
  {
    return videoDataListeners;
  }

  protected InetAddress getDroneAddress()
  {
    return droneAddress;
  }

  protected boolean isStopped()
  {
    return threadComponent.isStopped();
  }

  protected void setReady()
  {
    readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
  }

  public int getVideoDataPort()
  {
    return videoDataPort;
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

  protected abstract void doRun();
}