package com.tngtech.leapdrone.drone.data.enums;

public enum ARDrone2VideoCodec
{
  H264_360P(129),
  H264_720P(131);

  private final int codecValue;

  private ARDrone2VideoCodec(int codecValue)
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
