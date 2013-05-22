package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.config.DroneControllerConfig;

public class SetConfigValueCommand extends CommandAbstract
{
  private final String key;

  private final String value;

  public SetConfigValueCommand(String key, String value)
  {
    super(true);
    this.key = key;
    this.value = value;
  }


  @Override
  protected String getPreparationCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG_IDS=%d,\"%s\",\"%s\",\"%s\"", sequenceNumber, DroneControllerConfig.SESSION_ID,
            DroneControllerConfig.PROFILE_ID, DroneControllerConfig.APPLICATION_ID);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }
}
