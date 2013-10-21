package com.dronecontrol.droneapi.listeners;

import com.dronecontrol.droneapi.data.NavData;

public interface NavDataListener
{
  void onNavData(NavData navData);
}
