package com.tngtech.leapdrone.drone.listeners;

import com.tngtech.leapdrone.drone.data.NavData;

public interface NavDataListener
{
  void onNavData(NavData navData);
}
