package com.tngtech.leapdrone.input.leapmotion;

import com.google.inject.Inject;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

import com.tngtech.leapdrone.input.leapmotion.listeners.DetectionListener;
import com.tngtech.leapdrone.input.leapmotion.listeners.GestureListener;

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
    controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
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
  
  public void addGestureListener(GestureListener gestureListener)
  {
    listener.addGestureListener(gestureListener);
  }

  public void removeGestureListener(GestureListener gestureListener)
  {
    listener.removeGestureListener(gestureListener);
  }
}