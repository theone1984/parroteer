package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.data.Config;

public class SetConfigValueCommand extends CommandAbstract
{
  private final String key;

  private final String value;

  public SetConfigValueCommand(String key, Object value)
  {
    super(true);
    this.key = key;
    this.value = value.toString();
  }


  @Override
  protected String getPreparationCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG_IDS=%d,\"%s\",\"%s\",\"%s\"", sequenceNumber, Config.SESSION_ID,
            Config.PROFILE_ID, Config.APPLICATION_ID);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }
}
