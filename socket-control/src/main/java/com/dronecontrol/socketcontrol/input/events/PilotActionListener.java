package com.dronecontrol.socketcontrol.input.events;

import com.dronecontrol.socketcontrol.input.data.PilotAction;

public interface PilotActionListener
{
  void onPilotAction(PilotAction pilotAction);
}
