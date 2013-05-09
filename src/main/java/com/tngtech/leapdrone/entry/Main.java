package com.tngtech.leapdrone.entry;

import com.google.inject.Inject;
import com.tngtech.leapdrone.control.LeapDroneController;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.injection.Context;
import com.tngtech.leapdrone.input.leapmotion.LeapMotionController;
import com.tngtech.leapdrone.ui.SwingWindow;

import java.io.IOException;

public class Main
{
  private final SwingWindow swingWindow;

  private final DroneController droneController;

  private final LeapMotionController leapMotionController;

  private final LeapDroneController leapDroneController;

  public static void main(String[] args)
  {
    Context.getBean(Main.class).start();
  }

  @Inject
  public Main(SwingWindow swingWindow, DroneController droneController, LeapMotionController leapMotionController,
              LeapDroneController leapDroneController)
  {
    this.swingWindow = swingWindow;
    this.droneController = droneController;
    this.leapMotionController = leapMotionController;
    this.leapDroneController = leapDroneController;
  }

  private void start()
  {
    droneController.addNavDataListener(leapDroneController);
    leapMotionController.addDetectionListener(leapDroneController);

    droneController.start();
    swingWindow.createWindow();
    leapMotionController.connect();

    keepProcessBusy();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
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