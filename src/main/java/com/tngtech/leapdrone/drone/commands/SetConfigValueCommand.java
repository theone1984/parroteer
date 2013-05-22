package com.tngtech.leapdrone.drone.commands;

public class SetConfigValueCommand extends CommandAbstract
{
  private final String key;

  private final String value;

  public SetConfigValueCommand(String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }
}
