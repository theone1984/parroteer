package com.tngtech.leapdrone.control.leapmotion;

import com.google.inject.Inject;
import com.leapmotion.leap.Controller;
import com.tngtech.leapdrone.control.leapmotion.listeners.DetectionListener;

public class LeapMotionController
{
  private LeapMotionListener listener;

  private Controller controller;

  @Inject
  public LeapMotionController(LeapMotionListener listener, Controller controller)
  {
    this.listener = listener;
    this.controller = controller;
  }

  public void connect()
  {
    controller.addListener(listener);
  }

  public void disconnect()
  {
    controller.removeListener(listener);
  }

  public void addDetectionListener(DetectionListener detectionListener)
  {
    listener.addDetectionListener(detectionListener);
  }

  public void removeDetectionListener(DetectionListener detectionListener)
  {
    listener.removeDetectionListener(detectionListener);
  }
}