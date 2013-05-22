package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.config.DroneControllerConfig;
import com.tngtech.leapdrone.drone.data.VideoData;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.drone.video.P264ImageDecoder;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import com.tngtech.leapdrone.helpers.components.UdpComponent;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;

import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class ArDroneOneVideoRetriever extends VideoRetrieverAbstract
{
  private final Logger logger = Logger.getLogger(ArDroneOneVideoRetriever.class.getSimpleName());

  public static final int RECEIVING_BUFFER_SIZE = 1024000;

  private final UdpComponent udpComponent;

  private final P264ImageDecoder imageDecoder;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  @Inject
  public ArDroneOneVideoRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                                  ReadyStateComponent readyStateComponent, P264ImageDecoder imageDecoder)
  {
    super(threadComponent, addressComponent, readyStateComponent);
    this.udpComponent = udpComponent;
    this.imageDecoder = imageDecoder;

    determineDatagramPackets();
  }

  private void determineDatagramPackets()
  {
    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length, getDroneAddress(), DroneControllerConfig.VIDEO_DATA_PORT);
  }

  @Override
  public void run()
  {
    connectToVideoDataPort();
    initializeCommunication();
    setReady();

    while (!isStopped())
    {
      try
      {
        udpComponent.receive(incomingDataPacket);
        processData();

        udpComponent.sendKeepAlivePacket();
      } catch (RuntimeException e)
      {
        // This happens sometimes, but does not hinder the video data from being displayed
      }
    }

    disconnectFromVideoDataPort();
  }

  private void connectToVideoDataPort()
  {
    logger.info(String.format("Connecting to video data port %d", DroneControllerConfig.VIDEO_DATA_PORT));
    udpComponent.connect(getDroneAddress(), DroneControllerConfig.VIDEO_DATA_PORT);
  }

  private void initializeCommunication()
  {
    udpComponent.sendKeepAlivePacket();
    sleep(1000);
  }

  private void processData()
  {
    VideoData videoData = getVideoData();
    logger.trace(String.format("Received video data - width: %d, height: %d, bytes: %d", videoData.getWidth(), videoData.getHeight(),
            videoData.getPixelData().length));

    for (VideoDataListener listener : getVideoDataListeners())
    {
      listener.onVideoData(videoData);
    }
  }

  public VideoData getVideoData()
  {
    imageDecoder.determineImageFromStream(receivingBuffer, incomingDataPacket.getLength());
    int width = imageDecoder.getWidth();
    int height = imageDecoder.getHeight();
    int[] pixelData = imageDecoder.getJavaPixelData();

    return new VideoData(width, height, pixelData);
  }

  private void disconnectFromVideoDataPort()
  {
    logger.info(String.format("Disconnecting from video data port %d", DroneControllerConfig.VIDEO_DATA_PORT));
    udpComponent.disconnect();
  }
}