package com.tngtech.internal.droneapi.commands.simple;

import static com.tngtech.internal.droneapi.helpers.BinaryDataHelper.getNormalizedIntValue;

public class FlightMoveCommand extends ATCommandAbstract
{
  private final float roll;

  private final float pitch;

  private final float yaw;

  private final float gaz;

  public FlightMoveCommand(float roll, float pitch, float yaw, float gaz)
  {
    super(false);
    this.roll = roll;
    this.pitch = pitch;
    this.yaw = yaw;
    this.gaz = gaz;
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*PCMD=%d,%d,%d,%d,%d,%d", sequenceNumber, 1, getNormalizedIntValue(roll), getNormalizedIntValue(pitch),
            getNormalizedIntValue(gaz), getNormalizedIntValue(yaw));
  }
}
