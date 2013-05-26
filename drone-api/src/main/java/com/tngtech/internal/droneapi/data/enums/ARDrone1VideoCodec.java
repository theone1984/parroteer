package com.tngtech.internal.droneapi.data.enums;

public enum ARDrone1VideoCodec
{
  P264(64);

  private final int codecValue;

  private ARDrone1VideoCodec(int codecValue)
  {
    this.codecValue = codecValue;
  }

  public int getCodecCode()
  {
    return codecValue;
  }

  @Override
  public String toString()
  {
    return String.valueOf(codecValue);
  }
}