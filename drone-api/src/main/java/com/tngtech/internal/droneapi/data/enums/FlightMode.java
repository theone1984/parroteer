package com.tngtech.internal.droneapi.data.enums;

public enum FlightMode
{
  TAKE_OFF(290718208),
  LAND(290717696),
  EMERGENCY(290717952);

  private final int commandCode;

  FlightMode(int commandCode)
  {
    this.commandCode = commandCode;
  }

  public int getCommandCode()
  {
    return commandCode;
  }
}
