package com.tngtech.leapdrone.drone.data;

public enum DroneVersion
{
  AR_DRONE_1,
  AR_DRONE_2,
  UNKNOWN;

  public static final String VERSION_SEPARATOR = "\\.";

  public static DroneVersion fromVersionNumber(String versionNumber)
  {
    int majorVersion = getMajorVersion(versionNumber);
    return majorVersion == 2 ? AR_DRONE_2 : majorVersion == 1 ? AR_DRONE_1 : UNKNOWN;
  }

  public static int getMajorVersion(String versionNumber)
  {
    String[] versionDetails = versionNumber.split(VERSION_SEPARATOR);

    try
    {
      return Integer.parseInt(versionDetails[0]);
    } catch (NumberFormatException e)
    {
      throw new IllegalStateException("The version file did not contain the drone version");
    }
  }
}
