package com.dronecontrol.droneapi.commands;

import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.NavData;

public interface Command
{
  int NO_TIMEOUT = 0;

  int DEFAULT_NAVDATA_TIMEOUT = 100;

  int DEFAULT_CONFIGURATION_TIMEOUT = 1250;

  int getTimeoutMillis();

  void checkSuccess(NavData navData, DroneConfiguration droneConfiguration);
}