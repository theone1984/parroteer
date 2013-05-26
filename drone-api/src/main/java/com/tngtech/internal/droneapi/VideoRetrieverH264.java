package com.tngtech.internal.droneapi;

import com.google.inject.Inject;
import com.tngtech.internal.droneapi.components.AddressComponent;
import com.tngtech.internal.droneapi.components.ErrorListenerComponent;
import com.tngtech.internal.droneapi.components.ReadyStateListenerComponent;
import com.tngtech.internal.droneapi.components.TcpComponent;
import com.tngtech.internal.droneapi.components.ThreadComponent;
import com.tngtech.internal.droneapi.listeners.ImageListener;
import com.tngtech.internal.droneapi.listeners.VideoDataListener;
import com.tngtech.internal.droneapi.video.H264VideoDecoder;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

import static com.tngtech.internal.droneapi.helpers.ThreadHelper.sleep;

public class VideoRetrieverH264 extends VideoRetrieverAbstract implements ImageListener
{
  private final Logger logger = Logger.getLogger(VideoRetrieverP264.class);

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