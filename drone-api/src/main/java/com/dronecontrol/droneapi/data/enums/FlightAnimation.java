package com.dronecontrol.droneapi.data.enums;

public enum FlightAnimation
{
  PHI_M30_DEG(0, 1000),
  PHI_30_DEG(1, 1000),
  THETA_M30_DEG(2, 1000),
  THETA_30_DEG(3, 1000),
  THETA_20_DEG_YAW_200_DEG(4, 1000),
  THETA_20_DEG_YAW_M200_DEG(5, 1000),
  TURNAROUND(6, 5000),
  TURNAROUND_GODOWN(7, 5000),
  YAW_SHAKE(8, 2000),
  YAW_DANCE(9, 5000),
  PHI_DANCE(10, 5000),
  THETA_DANCE(11, 5000),
  VZ_DANCE(12, 5000),
  WAVE(13, 5000),
  PHI_THETA_MIXED(14, 5000),
  DOUBLE_PHI_THETA_MIXED(15, 5000),
  FLIP_AHEAD(16, 15),
  FLIP_BEHIND(17, 15),
  FLIP_LEFT(18, 15),
  FLIP_RIGHT(19, 15);

  private final int commandCode;

  private final int timeout;

  private FlightAnimation(int commandCode, int timeout)
  {
    this.commandCode = commandCode;
    this.timeout = timeout;
  }

  public int getCommandCode()
  {
    return commandCode;
  }

  public int getTimeout()
  {
    return timeout;
  }
}