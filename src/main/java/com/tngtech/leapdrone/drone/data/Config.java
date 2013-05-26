package com.tngtech.leapdrone.drone.data;

import com.tngtech.leapdrone.drone.data.enums.ARDrone1VideoCodec;
import com.tngtech.leapdrone.drone.data.enums.ARDrone2VideoCodec;

public final class Config
{
  private static final int NO_RETRIES = 0;

  public static final int WAIT_TIMEOUT = 15;

  public static final int REACHABLE_TIMEOUT = 1000;

  public static final String MIN_FIRMWARE_VERSION = "1.6.4";

  private final LoginData loginData;

  private final int maxStartupRetries;

  private String droneIpAddress = "192.168.1.1";

  private int ftpPort = 5551;

  private int navDataPort = 5554;

  private int videoDataPort = 5555;

  private int commandPort = 5556;

  private int configDataPort = 5559;

  private ARDrone1VideoCodec arDrone1VideoCodec = ARDrone1VideoCodec.P264;

  private ARDrone2VideoCodec arDrone2VideoCodec = ARDrone2VideoCodec.H264_360P;

  public Config(String applicationName, String profileName)
  {
    this(applicationName, profileName, NO_RETRIES);
  }

  public Config(String applicationName, String profileName, int maxStartupRetries)
  {
    this.maxStartupRetries = maxStartupRetries;
    loginData = new LoginData(applicationName, profileName);
  }

  public LoginData getLoginData()
  {
    return loginData;
  }

  public int getMaxStartupRetries()
  {
    return maxStartupRetries;
  }

  public String getDroneIpAddress()
  {
    return droneIpAddress;
  }

  public void setDroneIpAddress(String droneIpAddress)
  {
    this.droneIpAddress = droneIpAddress;
  }

  public int getFtpPort()
  {
    return ftpPort;
  }

  public void setFtpPort(int ftpPort)
  {
    this.ftpPort = ftpPort;
  }

  public int getNavDataPort()
  {
    return navDataPort;
  }

  public void setNavDataPort(int navDataPort)
  {
    this.navDataPort = navDataPort;
  }

  public int getVideoDataPort()
  {
    return videoDataPort;
  }

  public void setVideoDataPort(int videoDataPort)
  {
    this.videoDataPort = videoDataPort;
  }

  public int getCommandPort()
  {
    return commandPort;
  }

  public void setCommandPort(int commandPort)
  {
    this.commandPort = commandPort;
  }

  public int getConfigDataPort()
  {
    return configDataPort;
  }

  public void setConfigDataPort(int configDataPort)
  {
    this.configDataPort = configDataPort;
  }

  public ARDrone1VideoCodec getArDrone1VideoCodec()
  {
    return arDrone1VideoCodec;
  }

  public void setArDrone1VideoCodec(ARDrone1VideoCodec arDrone1VideoCodec)
  {
    this.arDrone1VideoCodec = arDrone1VideoCodec;
  }

  public ARDrone2VideoCodec getArDrone2VideoCodec()
  {
    return arDrone2VideoCodec;
  }

  public void setArDrone2VideoCodec(ARDrone2VideoCodec arDrone2VideoCodec)
  {
    this.arDrone2VideoCodec = arDrone2VideoCodec;
  }
}