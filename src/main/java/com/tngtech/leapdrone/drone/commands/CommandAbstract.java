package com.tngtech.leapdrone.drone.commands;

public abstract class CommandAbstract implements Command
{
  private static final String CARRIAGE_RETURN = "\r";

  @Override
  public String getCommandText(int sequenceNumber)
  {
    return getCommand(sequenceNumber) + CARRIAGE_RETURN;
  }

  protected abstract String getCommand(int sequenceNumber);
}