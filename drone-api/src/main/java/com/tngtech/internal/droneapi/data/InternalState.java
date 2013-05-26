package com.tngtech.internal.droneapi.data;

public class InternalState
{
  private boolean takeOffRequested;

  private boolean landRequested;

  private boolean emergencyRequested;

  private boolean flatTrimRequested;

  private boolean moveRequested;

  private float requestedRoll;

  private float requestedPitch;

  private float requestedYaw;

  private float requestedGaz;

  public boolean isTakeOffRequested()
  {
    return takeOffRequested;
  }

  public void setTakeOffRequested(boolean takeOffRequested)
  {
    this.takeOffRequested = takeOffRequested;
  }

  public boolean isLandRequested()
  {
    return landRequested;
  }

  public void setLandRequested(boolean landRequested)
  {
    this.landRequested = landRequested;
  }

  public boolean isEmergencyRequested()
  {
    return emergencyRequested;
  }

  public void setEmergencyRequested(boolean emergencyRequested)
  {
    this.emergencyRequested = emergencyRequested;
  }

  public boolean isFlatTrimRequested()
  {
    return flatTrimRequested;
  }

  public void setFlatTrimRequested(boolean flatTrimRequested)
  {
    this.flatTrimRequested = flatTrimRequested;
  }

  public boolean isMoveRequested()
  {
    return moveRequested;
  }

  public void setMoveRequested(boolean moveRequested)
  {
    this.moveRequested = moveRequested;
  }

  public float getRequestedRoll()
  {
    return requestedRoll;
  }

  public void setRequestedRoll(float requestedRoll)
  {
    this.requestedRoll = requestedRoll;
  }

  public float getRequestedPitch()
  {
    return requestedPitch;
  }

  public void setRequestedPitch(float requestedPitch)
  {
    this.requestedPitch = requestedPitch;
  }

  public float getRequestedYaw()
  {
    return requestedYaw;
  }

  public void setRequestedYaw(float requestedYaw)
  {
    this.requestedYaw = requestedYaw;
  }

  public float getRequestedGaz()
  {
    return requestedGaz;
  }

  public void setRequestedGaz(float requestedGaz)
  {
    this.requestedGaz = requestedGaz;
  }
}