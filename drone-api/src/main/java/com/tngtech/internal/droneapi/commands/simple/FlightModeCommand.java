package com.tngtech.internal.droneapi.commands.simple;

import com.tngtech.internal.droneapi.data.enums.FlightMode;

public class FlightModeCommand extends ATCommandAbstract
{
  private final FlightMode flightMode;

  public FlightModeCommand(FlightMode flightMode)
  {
    super(false);
    this.flightMode = flightMode;
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*REF=%d,%d", sequenceNumber, flightMode.getCommandCode());
  }
}
