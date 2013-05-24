package com.tngtech.leapdrone.drone.commands;

import static com.google.common.base.Preconditions.checkState;

public class SetConfigValueCommand extends CommandAbstract
{
  private final String sessionId;

  private final String profileId;

  private final String applicationId;

  private final String key;

  private final String value;

  public SetConfigValueCommand(String sessionId, String profileId, String applicationId)
  {
    super(true);
    this.sessionId = sessionId;
    this.profileId = profileId;
    this.applicationId = applicationId;
    this.key = null;
    this.value = null;
  }

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
    checkState(key != null && value != null, "Cannot get the command text with no key or value set");
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }
}
