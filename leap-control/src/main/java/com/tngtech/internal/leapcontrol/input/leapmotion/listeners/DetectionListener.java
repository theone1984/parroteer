package com.tngtech.internal.leapcontrol.input.leapmotion.listeners;

import com.tngtech.internal.leapcontrol.input.leapmotion.data.DetectionData;

public interface DetectionListener
{
  void onDetect(DetectionData detectionData);

  void onNoDetect();
}