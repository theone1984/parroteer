package com.tngtech.leapdrone.drone.data;

public class NavData
{
  private NavDataState state;

  private boolean onlyHeaderPresent;

  private int batteryLevel;

  private int sequenceNumber;

  private float pitch;

  private float roll;

  private float yaw;

  private float altitude;

  private float speedX;

  private float speedY;

  private float speedZ;

  public NavData()
  {
    onlyHeaderPresent = true;
  }

  public boolean isOnlyHeaderPresent()
  {
    return onlyHeaderPresent;
  }

  public void setOnlyHeaderPresent(boolean onlyHeaderPresent)
  {
    this.onlyHeaderPresent = onlyHeaderPresent;
  }

  public void setSequenceNumber(int sequenceNumber)
  {
    this.sequenceNumber = sequenceNumber;
  }

  public int getSequenceNumber()
  {
    return sequenceNumber;
  }

  public NavDataState getState()
  {
    return state;
  }

  public void setState(NavDataState state)
  {
    this.state = state;
  }

  public int getBatteryLevel()
  {
    return batteryLevel;
  }

  public void setBatteryLevel(int batteryLevel)
  {
    this.batteryLevel = batteryLevel;
  }

  public void setPitch(float pitch)
  {
    this.pitch = pitch;
  }

  public float getPitch()
  {
    return pitch;
  }

  public void setRoll(float roll)
  {
    this.roll = roll;
  }

  public float getRoll()
  {
    return roll;
  }

  public void setYaw(float yaw)
  {
    this.yaw = yaw;
  }

  public float getYaw()
  {
    return yaw;
  }

  public float getAltitude()
  {
    return altitude;
  }

  public void setAltitude(float altitude)
  {
    this.altitude = altitude;
  }

  public void setSpeedX(float speedX)
  {
    this.speedX = speedX;
  }

  public float getSpeedX()
  {
    return speedX;
  }

  public void setSpeedY(float speedY)
  {
    this.speedY = speedY;
  }

  public float getSpeedY()
  {
    return speedY;
  }

  public void setSpeedZ(float speedZ)
  {
    this.speedZ = speedZ;
  }

  public float getSpeedZ()
  {
    return speedZ;
  }
}