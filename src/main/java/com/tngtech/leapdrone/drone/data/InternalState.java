package com.tngtech.leapdrone.drone.data;

public class InternalState
{
  private boolean takeOffRequested;

  private boolean landRequested;

  private boolean emergencyRequested;

  private boolean flatTrimRequested;

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
}