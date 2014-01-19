package com.dronecontrol.kinectcontrol.config;

import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

@PropertiesFiles("config")
public class Config
{
  @PropertyValue("socket.server.hostname")
  @DefaultValue("localhost")
  private String hostName;

  @PropertyValue("socket.server.port")
  @DefaultValue("4500")
  private Integer port;

  public String getHostName()
  {
    return hostName;
  }

  public Integer getPort()
  {
    return port;
  }
}
