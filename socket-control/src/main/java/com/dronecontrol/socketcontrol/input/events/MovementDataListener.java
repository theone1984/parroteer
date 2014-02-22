package com.dronecontrol.socketcontrol.input.events;

import com.dronecontrol.socketcontrol.input.data.MovementData;

public interface MovementDataListener
{
  void onMovementData(MovementData movementData);
}