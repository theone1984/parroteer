package com.tngtech.internal.droneapi.commands;

import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.NavData;

public interface Command
{
  int NO_TIMEOUT = 0;

  int DEFAULT_NAVDATA_TIMEOUT = 100;

  int DEFAULT_CONFIGURATION_TIMEOUT = 1250;

  int getTimeoutMillis();

  void checkSuccess(NavData navData, DroneConfiguration droneConfiguration);
}