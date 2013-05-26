package com.tngtech.leapdrone.drone.data.enums;

public enum ControlDataMode
{
  IDLE(0),
  GET_CONFIGURATION_DATA(4),
  RESET_ACK_FLAG(5);

  private final int controlModeCode;

  ControlDataMode(int controlModeCode)
  {
    this.controlModeCode = controlModeCode;
  }

  public int getControlModeCode()
  {
    return controlModeCode;
  }
}
