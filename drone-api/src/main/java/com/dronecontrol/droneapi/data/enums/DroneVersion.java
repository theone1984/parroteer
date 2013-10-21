package com.dronecontrol.droneapi.data.enums;

import static com.google.common.base.Preconditions.checkState;

public enum DroneVersion
{
  AR_DRONE_1,
  AR_DRONE_2;

  public static final String VERSION_SEPARATOR = "\\.";

  public static DroneVersion fromVersionNumber(String versionNumber)
  {
    int majorVersion = getMajorVersion(versionNumber);

    return majorVersion == 2 ? AR_DRONE_2 : AR_DRONE_1;
  }

  public static int getMajorVersion(String versionNumber)
  {
    int majorVersion;
    String[] versionDetails = versionNumber.split(VERSION_SEPARATOR);

    try
    {
      majorVersion = Integer.parseInt(versionDetails[0]);
    } catch (NumberFormatException e)
    {
      throw new IllegalStateException("The version file did not contain the drone version");
    }

    checkState(majorVersion == 1 || majorVersion == 2, "Major version must either be 1 or 2");
    return majorVersion;
  }
}