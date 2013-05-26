package com.tngtech.internal.droneapi.commands.composed;

import com.tngtech.internal.droneapi.commands.Command;
import com.tngtech.internal.droneapi.commands.simple.SetConfigValueATCommand;
import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.LoginData;
import com.tngtech.internal.droneapi.data.enums.FlightAnimation;

public class PlayFlightAnimationCommand extends SetConfigValueCommand
{
  private final FlightAnimation animation;

  public PlayFlightAnimationCommand(LoginData loginData, FlightAnimation animation)
  {
    super(loginData);
    this.animation = animation;
  }

  @Override
  protected Command getConfigValueCommand()
  {
    return new SetConfigValueATCommand(getLoginData(), DroneConfiguration.FLIGHT_ANIMATION_KEY, getAnimationValuesText());
  }

  private String getAnimationValuesText()
  {
    return String.format("%d,%d", animation.getCommandCode(), animation.getTimeout());
  }
}