package com.tngtech.internal.droneapi.helpers;

public class VersionHelper
{
  public static int compareVersions(String version1Text, String version2Text)
  {
    String[] versionValues1 = version1Text.split("\\.");
    String[] versionValues2 = version2Text.split("\\.");

    int i = 0;
    while (i < versionValues1.length && i < versionValues2.length && versionValues1[i].equals(versionValues2[i]))
    {
      i++;
    }

    if (i < versionValues1.length && i < versionValues2.length)
    {
      int diff = Integer.valueOf(versionValues1[i]).compareTo(Integer.valueOf(versionValues2[i]));
      return diff < 0 ? -1 : diff == 0 ? 0 : 1;
    }

    return versionValues1.length < versionValues2.length ? -1 : versionValues1.length == versionValues2.length ? 0 : 1;
  }
}