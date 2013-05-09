package com.tngtech.leapdrone.control.leapmotion;

import com.google.inject.Inject;
import com.leapmotion.leap.Controller;
import com.tngtech.leapdrone.control.leapmotion.listeners.DetectionListener;
import org.apache.log4j.Logger;


public class LeapMotionController
{
  private final Logger logger = Logger.getLogger(LeapMotionController.class.getSimpleName());

  private final LeapMotionListener listener;

  private final Controller controller;

  @Inject
  public LeapMotionController(LeapMotionListener listener, Controller controller)
  {
    this.listener = listener;
    this.controller = controller;
  }

  public void connect()
  {
    logger.info("Connecting to leap motion controller");
    controller.addListener(listener);
  }

  public void disconnect()
  {
    logger.info("Disconnecting from leap motion controller");
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