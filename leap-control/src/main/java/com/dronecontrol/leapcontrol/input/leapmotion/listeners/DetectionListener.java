package com.dronecontrol.leapcontrol.input.leapmotion.listeners;

import com.dronecontrol.leapcontrol.input.leapmotion.data.DetectionData;

public interface DetectionListener
{
  void onDetect(DetectionData detectionData);

  void onNoDetect();
}