package com.tngtech.internal.droneapi;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.internal.droneapi.data.enums.DroneVersion;
import com.tngtech.internal.droneapi.components.UrlConnectionComponent;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class VersionReader
{
  private final UrlConnectionComponent urlConnectionComponent;

  private static final String FTP_URL_SCHEMA = "ftp:";

  private static final String VERSION_FILE_NAME = "version.txt";

  @Inject
  public VersionReader(UrlConnectionComponent urlConnectionComponent)
  {
    this.urlConnectionComponent = urlConnectionComponent;
  }

  public DroneVersion getDroneVersion(String ipAddress, int port)
  {
    String ftpFilePath = String.format("%s//%s:%d/%s", FTP_URL_SCHEMA, ipAddress, port, VERSION_FILE_NAME);

    try
    {
      List<String> fileLines = getFileContent(ftpFilePath);
      String versionNumber = getVersionLine(fileLines);

      return DroneVersion.fromVersionNumber(versionNumber);
    } catch (Exception e)
    {
      throw new IllegalStateException("There was an error while determining the drone version", e);
    }
  }

  private List<String> getFileContent(String ftpFilePath)
  {
    urlConnectionComponent.connect(ftpFilePath);
    List<String> lines = Lists.newArrayList(urlConnectionComponent.readLines());
    urlConnectionComponent.disconnect();

    return lines;
  }

  private String getVersionLine(List<String> fileLines)
  {
    checkState(fileLines.size() == 1, "The version file must contain exactly one line");
    return fileLines.get(0);
  }
}