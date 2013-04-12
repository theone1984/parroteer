package com.tngtech.leapdrone.control;

public interface LeapMotionDetectionEventHandler
{
  void onEvent(float height, float pitch, float roll);
}