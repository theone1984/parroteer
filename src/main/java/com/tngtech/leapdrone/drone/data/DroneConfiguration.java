package com.tngtech.leapdrone.drone.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class DroneConfiguration
{
  public static final String SESSION_ID_KEY = "custom:session_id";

  public static final String PROFILE_ID_KEY = "custom:profile_id";

  public static final String APPLICATION_ID_KEY = "custom:application_id";

  public static final String ENABLE_NAV_DATA_KEY = "general:navdata_demo";

  public static final String VIDEO_CODEC_KEY = "video:video_codec";

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