package com.tngtech.leapdrone.drone.commands.composed;

import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.simple.SetConfigValueATCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;
import com.tngtech.leapdrone.drone.data.enums.FlightAnimation;

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