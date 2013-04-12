package com.tngtech.leapdrone.entry;

import com.google.inject.Inject;
import com.tngtech.leapdrone.control.LeapMotionController;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.spring.Context;
import com.tngtech.leapdrone.ui.SwingWindow;

public class Main
{
  private final SwingWindow swingWindow;

  private final DroneController controller;
  
  private final LeapMotionController leapMotion;

  public static void main(String[] args)
  {
    Context.getBean(Main.class).start();
  }

  @Inject
  public Main(SwingWindow swingWindow, DroneController controller, LeapMotionController leapMotion)
  {
    this.swingWindow = swingWindow;
    this.controller = controller;
    this.leapMotion = leapMotion;
  }

  private void start()
  {
    controller.connect();
    
    leapMotion.connect();

    swingWindow.createWindow();
  }
}
