package com.dronecontrol.droneapi.data;

import com.dronecontrol.droneapi.helpers.ChecksumHelper;

public class LoginData
{
  private final String sessionChecksum;

  private final String profileChecksum;

  private final String applicationChecksum;

  public LoginData(String applicationName, String profileName)
  {
    sessionChecksum = ChecksumHelper.createRandomCrc32Hex();
    applicationChecksum = ChecksumHelper.createCrc32Hex(applicationName);
    profileChecksum = ChecksumHelper.createCrc32Hex(profileName);
  }

  public String getSessionChecksum()
  {
    return sessionChecksum;
  }

  public String getProfileChecksum()
  {
    return profileChecksum;
  }

  public String getApplicationChecksum()
  {
    return applicationChecksum;
  }
}
