package com.tngtech.leapdrone.drone.commands.simple;

public class FlightModeCommand extends ATCommandAbstract
{
  private final FlightMode flightMode;

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
