package com.dronecontrol.droneapi.commands.composed;

import com.dronecontrol.droneapi.commands.Command;
import com.dronecontrol.droneapi.commands.simple.SetConfigValueATCommand;
import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.LoginData;
import com.dronecontrol.droneapi.data.enums.LedAnimation;

import static com.dronecontrol.droneapi.helpers.BinaryDataHelper.getNormalizedIntValue;

public class PlayLedAnimationCommand extends SetConfigValueCommand
{
  private final LedAnimation animation;

  private final float frequency;

  private final int durationSeconds;

  public PlayLedAnimationCommand(LoginData loginData, LedAnimation animation, float frequency, int durationSeconds)
  {
    super(loginData);

    this.animation = animation;
    this.frequency = frequency;
    this.durationSeconds = durationSeconds;
  }

  @Override
  protected Command getConfigValueCommand()
  {
    return new SetConfigValueATCommand(getLoginData(), DroneConfiguration.LED_ANIMATION_KEY, getAnimationValuesText());
  }

  private String getAnimationValuesText()
  {
    return String.format("%d,%d,%d", animation.getAnimationCode(), getNormalizedIntValue(frequency), durationSeconds);
  }
}