package com.tngtech.leapdrone.drone.commands;

public class FlatTrimCommand extends CommandAbstract
{
  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*FTRIM=%d", sequenceNumber);
  }
}
