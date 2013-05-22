package com.tngtech.leapdrone.drone.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class DroneConfiguration
{
  private Map<String, String> config;

  public DroneConfiguration(Map<String, String> config)
  {
    this.config = ImmutableMap.copyOf(config);
  }

  public Map<String, String> getConfig()
  {
    return config;
  }
}