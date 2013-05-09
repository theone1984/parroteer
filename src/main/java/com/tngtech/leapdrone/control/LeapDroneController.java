package com.tngtech.leapdrone.control;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.input.leapmotion.data.DetectionData;
import com.tngtech.leapdrone.input.leapmotion.listeners.DetectionListener;

public class LeapDroneController implements NavDataListener, DetectionListener
{
  // Max height in meters
  private static final float MAX_HEIGHT = 2.0f;

  private static final float HEIGHT_THRESHOLD = 0.25f;

  private final DroneController droneController;

  private boolean navDataReceived = false;

  private float currentHeight;

  @Inject
  public LeapDroneController(DroneController droneController)
  {
    this.droneController = droneController;
  }

  @Override
  public void onDetect(DetectionData data)
  {
    float desiredHeight = data.getHeight() * MAX_HEIGHT;
    float heightDelta = navDataReceived ? calculateHeightDelta(desiredHeight) : 0.0f;

    droneController.move(data.getRoll(), data.getPitch(), data.getYaw(), heightDelta);
  }

  @Override
  public void onNoDetect()
  {
    droneController.move(0.0f, 0.0f, 0.0f, 0.0f);
  }

  private float calculateHeightDelta(float desiredHeight)
  {
    return (desiredHeight - currentHeight) / MAX_HEIGHT;
  }

  @Override
  public void onNavData(NavData navData)
  {
    navDataReceived = true;
    currentHeight = navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f : navData.getAltitude();
  }
}