package com.tngtech.leapdrone.drone.commands.composed;

import com.tngtech.leapdrone.drone.commands.ComposedCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;

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
