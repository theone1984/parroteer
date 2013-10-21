package com.dronecontrol.droneapi.listeners;

import com.dronecontrol.droneapi.data.DroneConfiguration;

public interface DroneConfigurationListener
{
  void onDroneConfiguration(DroneConfiguration config);
}