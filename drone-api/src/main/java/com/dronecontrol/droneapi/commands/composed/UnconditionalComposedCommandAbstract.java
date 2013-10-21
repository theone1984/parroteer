package com.dronecontrol.droneapi.commands.composed;

import com.dronecontrol.droneapi.commands.ComposedCommand;
import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.NavData;

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
