package com.dronecontrol.kinectcontrol.input.data;

@SuppressWarnings("UnusedDeclaration")
public class DroneData
{
  private boolean flying;

  private float currentHeight;

  public DroneData(boolean flying, float currentHeight)
  {
    this.flying = flying;
    this.currentHeight = currentHeight;
  }

  public boolean isFlying()
  {
    return flying;
  }

  public void setFlying(boolean flying)
  {
    this.flying = flying;
  }

  public float getCurrentHeight()
  {
    return currentHeight;
  }

  public void setCurrentHeight(float currentHeight)
  {
    this.currentHeight = currentHeight;
  }
}