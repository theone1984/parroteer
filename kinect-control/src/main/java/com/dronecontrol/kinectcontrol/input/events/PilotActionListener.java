package com.dronecontrol.kinectcontrol.input.events;

import com.dronecontrol.kinectcontrol.input.data.PilotAction;

public interface PilotActionListener
{
  void onPilotAction(PilotAction pilotAction);
}
