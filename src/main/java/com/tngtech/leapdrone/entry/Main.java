package com.tngtech.leapdrone.entry;

import com.google.inject.Inject;
import com.tngtech.leapdrone.control.LeapMotionController;
import com.tngtech.leapdrone.control.LeapMotionDetectionEventHandler;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.spring.Context;
import com.tngtech.leapdrone.ui.SwingWindow;

public class Main
{
  private final SwingWindow swingWindow;

  private final DroneController droneController;

  private final LeapMotionController leapMotion;

  public static void main(String[] args)
  {
    Context.getBean(Main.class).start();
  }

  @Inject
  public Main(SwingWindow swingWindow, DroneController droneController, LeapMotionController leapMotion)
  {
    this.swingWindow = swingWindow;
    this.droneController = droneController;
    this.leapMotion = leapMotion;
  }

  private void start()
  {
    droneController.connect();
    leapMotion.connect();
    addEventHandler();


    swingWindow.createWindow();
  }

  private void addEventHandler()
  {
    leapMotion.setDetectionEventHandler(new LeapMotionDetectionEventHandler()
    {
      @Override
      public void onEvent(float height, float pitch, float roll)
      {
        droneController.move(roll, pitch, 0.0f, height);
      }
    });
  }
}
