package com.dronecontrol.droneapi.commands.composed;

import com.dronecontrol.droneapi.commands.Command;
import com.dronecontrol.droneapi.commands.simple.SetConfigValueATCommand;
import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.LoginData;
import com.dronecontrol.droneapi.data.enums.FlightAnimation;

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