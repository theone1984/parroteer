package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;

public interface Command
{
  int NO_TIMEOUT = 0;

  int DEFAULT_NAVDATA_TIMEOUT = 100;

  int DEFAULT_CONFIGURATION_TIMEOUT = 1250;

  int getTimeoutMillis();

  void checkSuccess(NavData navData, DroneConfiguration droneConfiguration);
}