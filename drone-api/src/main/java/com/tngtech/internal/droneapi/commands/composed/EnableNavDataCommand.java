package com.tngtech.internal.droneapi.commands.composed;

import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.LoginData;
import com.tngtech.internal.droneapi.data.NavData;

import static com.google.common.base.Preconditions.checkState;

public class EnableNavDataCommand extends SetConfigValueCommand
{
  public EnableNavDataCommand(LoginData loginData)
  {
    super(loginData, DroneConfiguration.ENABLE_NAV_DATA_KEY, "TRUE");
  }

  @Override
  public int getTimeoutMillis()
  {
    return DEFAULT_NAVDATA_TIMEOUT;
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    checkState(!navData.isOnlyHeaderPresent(), "The nav data state is not sent yet");
  }
}