package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.config.DroneControllerConfig;
import com.tngtech.leapdrone.drone.listeners.ImageListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.drone.video.H264VideoDecoder;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.TcpComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

public class ArDroneTwoVideoRetriever extends VideoRetrieverAbstract implements ImageListener
{
  private final Logger logger = Logger.getLogger(ArDroneOneVideoRetriever.class.getSimpleName());

  private final TcpComponent tcpComponent;

  private final H264VideoDecoder videoDecoder;

  @Inject
  public ArDroneTwoVideoRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, TcpComponent tcpComponent,
                                  ReadyStateComponent readyStateComponent, H264VideoDecoder videoDecoder)
  {
    super(threadComponent, addressComponent, readyStateComponent);
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
  public void run()
  {
    connectToVideoDataPort();
    initializeCommunication();
    setReady();

    videoDecoder.startDecoding(tcpComponent.getInputStream(), this);

    disconnectFromVideoDataPort();
  }

  private void connectToVideoDataPort()
  {
    logger.info(String.format("Connecting to video data port %d", DroneControllerConfig.VIDEO_DATA_PORT));
    tcpComponent.connect(getDroneAddress(), DroneControllerConfig.VIDEO_DATA_PORT);
  }

  private void initializeCommunication()
  {
    tcpComponent.sendKeepAlivePacket();
  }

  private void disconnectFromVideoDataPort()
  {
    logger.info(String.format("Disconnecting from video data port %d", DroneControllerConfig.VIDEO_DATA_PORT));
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