package com.tngtech.internal.leapcontrol.injection;

import com.google.inject.Provider;
import com.tngtech.internal.droneapi.DroneController;

public class DroneControllerProvider implements Provider<DroneController>
{
  public DroneController get()
  {
    return DroneController.build();
  }
}