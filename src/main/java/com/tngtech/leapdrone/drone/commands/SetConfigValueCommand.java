package com.tngtech.leapdrone.drone.commands;

public class SetConfigValueCommand extends CommandAbstract
{
  private final String sessionId;

  private final String profileId;

  private final String applicationId;

  private final String key;

  private final String value;

  public SetConfigValueCommand(String sessionId, String profileId, String applicationId, String key, Object value)
  {
    super(true);
    this.sessionId = sessionId;
    this.profileId = profileId;
    this.applicationId = applicationId;
    this.key = key;
    this.value = value.toString();
  }

  @Override
  protected String getPreparationCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG_IDS=%d,\"%s\",\"%s\",\"%s\"", sequenceNumber, sessionId, profileId, applicationId);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }
}
