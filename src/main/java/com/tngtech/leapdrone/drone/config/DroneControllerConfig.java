package com.tngtech.leapdrone.drone.config;

public final class DroneControllerConfig
{
  public enum DroneVersion
  {
    ARDRONE_1, ARDRONE_2
  }

  public static final DroneVersion DRONE_VERSION = DroneVersion.ARDRONE_2;

  // Must be an 8-character hex field
  public static final String SESSION_ID = "affeaffe";

  // Must be an 8-character hex field
  public static final String APPLICATION_ID = "afafafaf";

  // Must be an 8-character hex field
  public static final String PROFILE_ID = "faeffaef";

  public static final String DRONE_IP_ADDRESS = "192.168.1.1";

  public static final int NAV_DATA_PORT = 5554;

  public static final int VIDEO_DATA_PORT = 5555;

  public static final int COMMAND_PORT = 5556;

  public static final int CONFIG_DATA_PORT = 5559;

  public static final int REACHABLE_TIMEOUT = 1000;

  private DroneControllerConfig()
  {
  }
}