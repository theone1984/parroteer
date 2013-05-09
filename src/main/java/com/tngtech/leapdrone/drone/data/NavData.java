package com.tngtech.leapdrone.drone.data;

public class NavData
{
  private final int batteryLevel;

  private final float altitude;

  public NavData(int batteryLevel, float altitude)
  {
    this.batteryLevel = batteryLevel;
    this.altitude = altitude;
  }

  public int getBatteryLevel()
  {
    return batteryLevel;
  }

  public float getAltitude()
  {
    return altitude;
  }
}