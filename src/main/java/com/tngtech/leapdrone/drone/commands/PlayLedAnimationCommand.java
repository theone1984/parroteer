package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;

import static com.tngtech.leapdrone.helpers.BinaryDataHelper.getNormalizedIntValue;

public class PlayLedAnimationCommand extends SetConfigValueCommand
{
  public PlayLedAnimationCommand(String sessionId, String profileId, String applicationId)
  {
    super(sessionId, profileId, applicationId);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, DroneConfiguration.LED_ANIMATION_KEY, getAnimationValuesText());
  }

  private String getAnimationValuesText()
  {
    return String.format("%d,%d,%d", 3, getNormalizedIntValue(2.0f), 3);
  }
}
