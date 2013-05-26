package com.tngtech.internal.droneapi.commands.simple;

public class FlatTrimCommand extends ATCommandAbstract
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
