package com.tngtech.leapdrone.drone.config;

public final class DroneConfig
{
  public enum DroneVersion
  {
    ARDRONE_1, ARDRONE_2
  }

  public static final DroneVersion DRONE_VERSION = DroneVersion.ARDRONE_2;

  public static final String DRONE_IP_ADDRESS = "192.168.1.1";

  public static final int NAVDATA_PORT = 5554;

  public static final int VIDEO_DATA_PORT = 5555;

  public static final int COMMAND_PORT = 5556;

  public static final int REACHABLE_TIMEOUT = 1000;

  private DroneConfig()
  {
  }
}
