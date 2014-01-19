package com.dronecontrol.kinectcontrol.input.events;

import com.dronecontrol.kinectcontrol.input.data.MovementData;

public interface MovementDataListener
{
  void onMovementData(MovementData movementData);
}