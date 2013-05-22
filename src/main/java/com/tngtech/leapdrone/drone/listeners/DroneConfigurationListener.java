package com.tngtech.leapdrone.drone.listeners;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;

public interface DroneConfigurationListener
{
  void onDroneConfiguration(DroneConfiguration config);
}