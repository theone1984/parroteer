package com.tngtech.internal.droneapi;

import com.google.inject.Inject;
import com.tngtech.internal.droneapi.components.AddressComponent;
import com.tngtech.internal.droneapi.components.ErrorListenerComponent;
import com.tngtech.internal.droneapi.components.ReadyStateListenerComponent;
import com.tngtech.internal.droneapi.components.ThreadComponent;
import com.tngtech.internal.droneapi.components.UdpComponent;
import com.tngtech.internal.droneapi.listeners.VideoDataListener;
import com.tngtech.internal.droneapi.video.P264ImageDecoder;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;

import static com.tngtech.internal.droneapi.helpers.ThreadHelper.sleep;

public class VideoRetrieverP264 extends VideoRetrieverAbstract
{
  private final Logger logger = Logger.getLogger(VideoRetrieverP264.class);

  public static final int RECEIVING_BUFFER_SIZE = 1024000;

  private final UdpComponent udpComponent;

  private final P264ImageDecoder imageDecoder;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  @Inject
  public VideoRetrieverP264(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                            ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                            P264ImageDecoder imageDecoder)
  {
    super(threadComponent, addressComponent, readyStateListenerComponent, errorListenerComponent);
    this.udpComponent = udpComponent;
    this.imageDecoder = imageDecoder;

    determineDatagramPackets();
  }

  private void determineDatagramPackets()
  {
    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length, getDroneAddress(), getVideoDataPort());
  }

  @Override
  protected void doRun()
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
    logger.info(String.format("Connecting to video data port %d", getVideoDataPort()));
    udpComponent.connect(getDroneAddress(), getVideoDataPort());
  }

  private void initializeCommunication()
  {
    udpComponent.sendKeepAlivePacket();
    sleep(1000);
  }

  private void processData()
  {
    BufferedImage image = getImage();

    logger.trace(String.format("Received video data - width: %d, height: %d", image.getWidth(), image.getHeight()));

    for (VideoDataListener listener : getVideoDataListeners())
    {
      listener.onVideoData(image);
    }
  }

  public BufferedImage getImage()
  {
    imageDecoder.determineImageFromStream(receivingBuffer, incomingDataPacket.getLength());
    int width = imageDecoder.getWidth();
    int height = imageDecoder.getHeight();

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    image.setRGB(0, 0, width, height, imageDecoder.getJavaPixelData(), 0, width);

    return image;
  }

  private void disconnectFromVideoDataPort()
  {
    logger.info(String.format("Disconnecting from video data port %d", getVideoDataPort()));
    udpComponent.disconnect();
  }
}