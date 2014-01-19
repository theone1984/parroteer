package com.dronecontrol.kinectcontrol.input.data;

@SuppressWarnings("UnusedDeclaration")
public class MovementData
{
  private float roll;

  private float pitch;

  private float yaw;

  private float gaz;

  public MovementData()
  {
  }

  public MovementData(float roll, float pitch, float yaw, float gaz)
  {
    this.roll = roll;
    this.pitch = pitch;
    this.yaw = yaw;
    this.gaz = gaz;
  }

  public float getRoll()
  {
    return roll;
  }

  public void setRoll(int roll)
  {
    this.roll = roll;
  }

  public float getPitch()
  {
    return pitch;
  }

  public void setPitch(int pitch)
  {
    this.pitch = pitch;
  }

  public float getYaw()
  {
    return yaw;
  }

  public void setYaw(int yaw)
  {
    this.yaw = yaw;
  }

  public float getGaz()
  {
    return gaz;
  }

  public void setGaz(int gaz)
  {
    this.gaz = gaz;
  }

  @Override
  public String toString()
  {
    return String.format("Roll: %.2f, Pitch: %.2f, Yaw: %.2f, Gaz: %.2f", roll, pitch, yaw, gaz);
  }
}