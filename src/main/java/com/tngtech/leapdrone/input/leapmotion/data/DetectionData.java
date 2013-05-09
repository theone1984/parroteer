package com.tngtech.leapdrone.input.leapmotion.data;

public class DetectionData
{
  private final float roll;

  private final float pitch;

  private final float yaw;

  private final float height;

  public DetectionData(float roll, float pitch, float yaw, float height)
  {
    this.roll = roll;
    this.pitch = pitch;
    this.yaw = yaw;
    this.height = height;
  }

  public float getRoll()
  {
    return roll;
  }

  public float getPitch()
  {
    return pitch;
  }

  public float getYaw()
  {
    return yaw;
  }

  public float getHeight()
  {
    return height;
  }
}