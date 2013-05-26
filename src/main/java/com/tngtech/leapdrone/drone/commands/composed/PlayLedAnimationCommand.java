package com.tngtech.leapdrone.drone.commands.composed;

import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.simple.SetConfigValueATCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;

import static com.tngtech.leapdrone.drone.helpers.BinaryDataHelper.getNormalizedIntValue;

public class PlayLedAnimationCommand extends SetConfigValueCommand
{
  public enum LedAnimation
  {
    BLINK_GREEN_RED(0),
    BLING_GREEN(1),
    BLINK_RED(2),
    BLINK_ORANGE(3),
    SNAKE_GREEN_RED(4),
    FIRE(5),
    STANDARD(6),
    RED(7),
    GREEN(8),
    RED_SNAKE(9),
    BLANK(10),
    RIGHT_MISSILE(11),
    LEFT_MISSILE(12),
    DOUBLE_MISSILE(13),
    FRONT_LEFT_GREEN_OTHERS_RED(14),
    FRONT_RIGHT_GREEN_OTHERS_RED(15),
    REAR_LEFT_GREEN_OTHERS_RED(17),
    REAR_RIGHT_GREEN_OTHERS_RED(16),
    LEFT_GREEN_RIGHT_RED(17),
    LEFT_RED_RIGHT_GREEN(18),
    BLINK_STANDARD(19);

    private final int animationCode;

    private LedAnimation(int animationCode)
    {
      this.animationCode = animationCode;
    }

    private int getAnimationCode()
    {
      return animationCode;
    }
  }

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