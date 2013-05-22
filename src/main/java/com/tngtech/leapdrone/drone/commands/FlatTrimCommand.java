package com.tngtech.leapdrone.drone.commands;

public class FlatTrimCommand extends CommandAbstract
{
  public FlatTrimCommand()
  {
    super(false);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*FTRIM=%d", sequenceNumber);
  }
}
