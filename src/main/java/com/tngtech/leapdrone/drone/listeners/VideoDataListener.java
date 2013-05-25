package com.tngtech.leapdrone.drone.listeners;

import java.awt.image.BufferedImage;

public interface VideoDataListener
{
  void onVideoData(BufferedImage droneImage);
}
