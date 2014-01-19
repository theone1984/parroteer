package com.dronecontrol.kinectcontrol.injection;

import com.google.inject.Provider;
import com.dronecontrol.droneapi.DroneController;

public class DroneControllerProvider implements Provider<DroneController>
{
  public DroneController get()
  {
    return DroneController.build();
  }
}