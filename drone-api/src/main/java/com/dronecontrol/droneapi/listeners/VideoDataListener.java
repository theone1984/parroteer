package com.dronecontrol.droneapi.listeners;

import java.awt.image.BufferedImage;

public interface VideoDataListener
{
  void onVideoData(BufferedImage droneImage);
}
