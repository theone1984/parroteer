package com.dronecontrol.kinectcontrol.input.data;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class PilotData
{
  private MovementData movementData;

  private Collection<PilotAction> pilotActions;

  public PilotData()
  {
  }

  public PilotData(MovementData movementData, Collection<PilotAction> pilotActions)
  {
    this.movementData = movementData;
    this.pilotActions = pilotActions;
  }

  public MovementData getMovementData()
  {
    return movementData;
  }

  public void setMovementData(MovementData movementData)
  {
    this.movementData = movementData;
  }

  public Collection<PilotAction> getPilotActions()
  {
    return pilotActions;
  }

  public void setPilotActions(Collection<PilotAction> pilotActions)
  {
    this.pilotActions = pilotActions;
  }
}
