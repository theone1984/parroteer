package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.Set;

public abstract class VideoRetrieverAbstract implements Runnable
{
  private final Logger logger = Logger.getLogger(VideoRetrieverAbstract.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final ReadyStateComponent readyStateComponent;

  private final InetAddress droneAddress;

  private final Set<VideoDataListener> videoDataListeners;

  @Inject
  public VideoRetrieverAbstract(ThreadComponent threadComponent, AddressComponent addressComponent, ReadyStateComponent readyStateComponent)
  {
    super();

    this.threadComponent = threadComponent;
    this.readyStateComponent = readyStateComponent;

    videoDataListeners = Sets.newLinkedHashSet();
    droneAddress = addressComponent.getInetAddress(Config.DRONE_IP_ADDRESS);
  }

  public void start()
  {
    logger.info("Starting video thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Stopping video thread");
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
    readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
  }
}