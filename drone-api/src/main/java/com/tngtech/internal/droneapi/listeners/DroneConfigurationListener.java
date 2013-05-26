package com.tngtech.internal.droneapi.listeners;

import com.tngtech.internal.droneapi.data.DroneConfiguration;

public interface DroneConfigurationListener
{
  void onDroneConfiguration(DroneConfiguration config);
}