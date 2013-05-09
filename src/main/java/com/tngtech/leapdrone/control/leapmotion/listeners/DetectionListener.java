package com.tngtech.leapdrone.control.leapmotion.listeners;

import com.tngtech.leapdrone.control.leapmotion.data.DetectionData;

public interface DetectionListener
{
  void onDetect(DetectionData detectionData);
}