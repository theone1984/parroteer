package com.tngtech.leapdrone.drone;


import com.codeminders.ardrone.ARDrone;
import com.google.inject.Inject;

import java.io.IOException;

public class DroneController
{
  private static final long CONNECT_TIMEOUT = 5000;

  private final ARDrone arDrone;

  @Inject
  public DroneController(ARDrone arDrone)
  {
    this.arDrone = arDrone;
  }

  public void connect()
  {
    try
    {
      arDrone.connect();
      arDrone.clearEmergencySignal();
      arDrone.waitForReady(CONNECT_TIMEOUT);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void takeOff()
  {
    try
    {
      arDrone.takeOff();
    } catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
  }

  public void land()
  {
    try
    {
      arDrone.land();
    } catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    try
    {
      arDrone.move(roll, pitch, yaw, gaz);
    } catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
  }
}