package com.tngtech.leapdrone.drone.listeners;

import com.tngtech.leapdrone.drone.data.VideoData;

import java.awt.image.BufferedImage;

public interface VideoDataListener
{
  void onVideoData(VideoData videoData);

  void onVideoData(BufferedImage droneImage);
}
