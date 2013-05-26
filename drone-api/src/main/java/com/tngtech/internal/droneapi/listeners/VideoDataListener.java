package com.tngtech.internal.droneapi.listeners;

import java.awt.image.BufferedImage;

public interface VideoDataListener
{
  void onVideoData(BufferedImage droneImage);
}
