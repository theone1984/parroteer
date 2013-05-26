package com.tngtech.internal.droneapi.components;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class AddressComponent
{
  private Map<String, InetAddress> knownInetAddresses;

  public AddressComponent()
  {
    knownInetAddresses = Maps.newHashMap();
  }

  public InetAddress getInetAddress(String hostName)
  {
    try
    {
      if (!knownInetAddresses.containsKey(hostName))
      {
        knownInetAddresses.put(hostName, InetAddress.getByName(hostName));
      }
      return knownInetAddresses.get(hostName);
    } catch (UnknownHostException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public boolean isReachable(String hostName, int timeout)
  {
    try
    {
      return getInetAddress(hostName).isReachable(timeout);
    } catch (IOException e)
    {
      return false;
    }
  }
}