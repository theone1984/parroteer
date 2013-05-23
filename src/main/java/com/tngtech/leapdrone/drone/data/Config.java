package com.tngtech.leapdrone.drone.data;

import com.tngtech.leapdrone.drone.data.enums.ARDrone1VideoCodec;
import com.tngtech.leapdrone.drone.data.enums.ARDrone2VideoCodec;

public final class Config
{
  public static final int WAIT_TIMEOUT = 15;

  // Must be an 8-character hex field
  public static final String SESSION_ID = "affeaffa";

  // Must be an 8-character hex field
  public static final String APPLICATION_ID = "afafafaf";

  // Must be an 8-character hex field
  public static final String PROFILE_ID = "faeffaef";

  public static final String DRONE_IP_ADDRESS = "192.168.1.1";

  public static final int FTP_PORT = 5551;

  public static final int NAV_DATA_PORT = 5554;

  public static final int VIDEO_DATA_PORT = 5555;

  public static final int COMMAND_PORT = 5556;

  public static final int CONFIG_DATA_PORT = 5559;

  public static final int REACHABLE_TIMEOUT = 1000;

  public static ARDrone1VideoCodec ARDRONE_1_VIDEO_CODEC = ARDrone1VideoCodec.P264;

  public static ARDrone2VideoCodec ARDRONE_2_VIDEO_CODEC = ARDrone2VideoCodec.H264_720P;

  private Config()
  {
  }
}