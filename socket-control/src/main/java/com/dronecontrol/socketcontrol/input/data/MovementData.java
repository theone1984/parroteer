package com.dronecontrol.socketcontrol.input.data;

@SuppressWarnings("UnusedDeclaration")
public class MovementData
{
  public static final MovementData NO_MOVEMENT = new MovementData(0, 0, 0, 0);

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

  public void setRoll(float roll)
  {
    this.roll = roll;
  }

  public float getPitch()
  {
    return pitch;
  }

  public void setPitch(float pitch)
  {
    this.pitch = pitch;
  }

  public float getYaw()
  {
    return yaw;
  }

  public void setYaw(float yaw)
  {
    this.yaw = yaw;
  }

  public float getGaz()
  {
    return gaz;
  }

  public void setGaz(float gaz)
  {
    this.gaz = gaz;
  }

  @Override
  public String toString()
  {
    return String.format("Roll: %.2f, Pitch: %.2f, Yaw: %.2f, Gaz: %.2f", roll, pitch, yaw, gaz);
  }
}