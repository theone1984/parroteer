package com.tngtech.leapdrone.drone;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.helpers.components.ThreadComponent;
import com.tngtech.leapdrone.helpers.components.UdpComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.data.VideoData;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.drone.video.BufferedVideoImage;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Set;

import static com.tngtech.leapdrone.helpers.ThreadHelper.sleep;

public class VideoRetriever implements Runnable
{
  public static final int RECEIVING_BUFFER_SIZE = 1024000;

  private final Logger logger = Logger.getLogger(VideoRetriever.class.getSimpleName());

  private final ThreadComponent threadComponent;

  private final AddressComponent addressComponent;

  private final UdpComponent udpComponent;

  private final BufferedVideoImage bufferedVideoImage;

  private final Set<VideoDataListener> videoDataListeners;

  private byte[] receivingBuffer;

  private DatagramPacket incomingDataPacket;

  @Inject
  public VideoRetriever(ThreadComponent threadComponent, AddressComponent addressComponent, UdpComponent udpComponent,
                        BufferedVideoImage bufferedVideoImage)
  {
    super();

    this.threadComponent = threadComponent;
    this.addressComponent = addressComponent;
    this.udpComponent = udpComponent;
    this.bufferedVideoImage = bufferedVideoImage;
    videoDataListeners = Sets.newLinkedHashSet();

    determineDatagramPackets();
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

  private void determineDatagramPackets()
  {
    InetAddress address = addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    receivingBuffer = new byte[RECEIVING_BUFFER_SIZE];
    incomingDataPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length, address, DroneConfig.VIDEO_DATA_PORT);
  }

  @Override
  public void run()
  {
    connectToVideoDataPort();
    initializeCommunication();

    while (!threadComponent.isStopped())
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
    InetAddress address = addressComponent.getInetAddress(DroneConfig.DRONE_IP_ADDRESS);

    logger.info(String.format("Connecting to video data port %d", DroneConfig.VIDEO_DATA_PORT));
    udpComponent.connect(address, DroneConfig.VIDEO_DATA_PORT);
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

    for (VideoDataListener listener : videoDataListeners)
    {
      listener.onVideoData(videoData);
    }
  }

  public VideoData getVideoData()
  {
    bufferedVideoImage.addImageStream(receivingBuffer, incomingDataPacket.getLength());

    int width = bufferedVideoImage.getWidth();
    int height = bufferedVideoImage.getHeight();
    int[] pixelData = bufferedVideoImage.getJavaPixelData();

    return new VideoData(width, height, pixelData);
  }

  private void disconnectFromVideoDataPort()
  {
    logger.info(String.format("Disconnecting from video data port %d", DroneConfig.VIDEO_DATA_PORT));
    udpComponent.disconnect();
  }
}