package com.dronecontrol.droneapi.data.enums;

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
  REAR_LEFT_GREEN_OTHERS_RED(16),
  REAR_RIGHT_GREEN_OTHERS_RED(17),
  LEFT_GREEN_RIGHT_RED(18),
  LEFT_RED_RIGHT_GREEN(19),
  BLINK_STANDARD(20);

  private final int animationCode;

  private LedAnimation(int animationCode)
  {
    this.animationCode = animationCode;
  }

  public int getAnimationCode()
  {
    return animationCode;
  }
}
