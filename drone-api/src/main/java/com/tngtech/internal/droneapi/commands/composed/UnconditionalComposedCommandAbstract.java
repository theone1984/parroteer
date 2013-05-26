package com.tngtech.internal.droneapi.commands.composed;

import com.tngtech.internal.droneapi.commands.ComposedCommand;
import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.NavData;

public abstract class UnconditionalComposedCommandAbstract implements ComposedCommand
{
  @Override
  public int getTimeoutMillis()
  {
    return 0;
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    // Nothing to do here
  }
}
