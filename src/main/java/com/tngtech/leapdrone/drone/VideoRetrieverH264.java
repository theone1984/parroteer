package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.drone.components.ReadyStateListenerComponent;
import com.tngtech.leapdrone.drone.components.TcpComponent;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.listeners.ImageListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.drone.video.H264VideoDecoder;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

import static com.tngtech.leapdrone.drone.helpers.ThreadHelper.sleep;

public class VideoRetrieverH264 extends VideoRetrieverAbstract implements ImageListener
{
  private final Logger logger = Logger.getLogger(VideoRetrieverP264.class.getSimpleName());

  private final TcpComponent tcpComponent;

  private final H264VideoDecoder videoDecoder;

  @Inject
  public VideoRetrieverH264(ThreadComponent threadComponent, AddressComponent addressComponent, TcpComponent tcpComponent,
                            ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                            H264VideoDecoder videoDecoder)
  {
    super(threadComponent, addressComponent, readyStateListenerComponent, errorListenerComponent);
    this.tcpComponent = tcpComponent;
    this.videoDecoder = videoDecoder;
  }

  @Override
  public void stop()
  {
    super.stop();
    videoDecoder.stopDecoding();
  }

  @Override
  protected void doRun()
  {
    connectToVideoDataPort();
    initializeCommunication();
    setReady();

    while (!isStopped())
    {
      videoDecoder.startDecoding(tcpComponent, this);
      if (!isStopped())
      {
        reconnectVideoPort();
      }
    }

    disconnectFromVideoDataPort();
  }

  private void reconnectVideoPort()
  {
    logger.warn("Reconnecting video data port");
    tcpComponent.disconnect();
    sleep(4000);
    tcpComponent.connect(getDroneAddress(), getVideoDataPort());
  }

  private void connectToVideoDataPort()
  {
    logger.info(String.format("Connecting to video data port %d", getVideoDataPort()));
    tcpComponent.connect(getDroneAddress(), getVideoDataPort());
  }

  private void initializeCommunication()
  {
    tcpComponent.sendKeepAlivePacket();
  }

  private void disconnectFromVideoDataPort()
  {
    logger.info(String.format("Disconnecting from video data port %d", getVideoDataPort()));
    tcpComponent.disconnect();
  }

  @Override
  public void onImage(BufferedImage image)
  {
    for (VideoDataListener listener : getVideoDataListeners())
    {
      listener.onVideoData(image);
    }
  }
}