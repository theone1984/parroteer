package com.tngtech.leapdrone.drone.data;

public enum ARDrone1VideoCodec
{
  P264(64);

  private final int codecValue;

  private ARDrone1VideoCodec(int codecValue)
  {
    this.codecValue = codecValue;
  }

  public int getCodecValue()
  {
    return codecValue;
  }
}