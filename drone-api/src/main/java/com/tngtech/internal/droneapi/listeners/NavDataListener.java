package com.tngtech.internal.droneapi.listeners;

import com.tngtech.internal.droneapi.data.NavData;

public interface NavDataListener
{
  void onNavData(NavData navData);
}
