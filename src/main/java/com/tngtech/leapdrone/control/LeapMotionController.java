package com.tngtech.leapdrone.control;

import com.leapmotion.leap.Controller;

public class LeapMotionController
{
  private LeapMotionListener listener;

  private Controller controller;



  public LeapMotionController()
  {
    this.listener = new LeapMotionListener();
    this.controller = new Controller();
  }

  public void connect()
  {
    // Have the sample listener receive events from the controller
    controller.addListener(listener);
  }

  public void disconnect()
  {
    // Have the sample listener receive events from the controller
    controller.removeListener(listener);
  }

  public void setDetectionEventHandler(LeapMotionDetectionEventHandler eventHandler)
  {
    listener.setDetectionEventHandler(eventHandler);
  }
}