package com.tngtech.leapdrone.entry;

import com.google.inject.Inject;
import com.tngtech.leapdrone.control.LeapMotionController;
import com.tngtech.leapdrone.control.LeapMotionDetectionEventHandler;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.injection.Context;
import com.tngtech.leapdrone.ui.SwingWindow;

import java.io.IOException;

public class Main
{
  private final SwingWindow swingWindow;

  private final DroneController droneController;

  private final LeapMotionController leapMotionController;

  public static void main(String[] args)
  {
    Context.getBean(Main.class).start();
  }

  @Inject
  public Main(SwingWindow swingWindow, DroneController droneController, LeapMotionController leapMotionController)
  {
    this.swingWindow = swingWindow;
    this.droneController = droneController;
    this.leapMotionController = leapMotionController;
  }

  private void start()
  {
    droneController.connect();

    swingWindow.createWindow();

    leapMotionController.connect();
    addEventHandler();

    keepProcessBusy();
  }

  private void addEventHandler()
  {
    leapMotionController.setDetectionEventHandler(new LeapMotionDetectionEventHandler()
    {
      @Override
      public void onEvent(float height, float pitch, float roll)
      {
        System.out.println(String.format("Detected: gaz = " + height + ", pitch = " + pitch + ", roll = " + roll));
        droneController.move(roll, pitch, 0.0f, height);
      }
    });
  }

  private void keepProcessBusy()
  {
    try
    {
      System.in.read();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
