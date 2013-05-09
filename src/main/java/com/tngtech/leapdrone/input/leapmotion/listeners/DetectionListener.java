package com.tngtech.leapdrone.input.leapmotion.listeners;

import com.tngtech.leapdrone.input.leapmotion.data.DetectionData;

public interface DetectionListener
{
  void onDetect(DetectionData detectionData);

  void onNoDetect();
}