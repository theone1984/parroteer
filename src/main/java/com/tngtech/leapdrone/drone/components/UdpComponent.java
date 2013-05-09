package com.tngtech.leapdrone.drone.components;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public class UdpComponent
{
  private Map<String, InetAddress> knownInetAddresses;

  private DatagramSocket socket;

  public UdpComponent()
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

  public void connect(int port)
  {
    try
    {
      socket = new DatagramSocket(port);
      socket.setSoTimeout(3000);
    } catch (SocketException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void send(DatagramPacket packet)
  {
    try
    {
      socket.send(packet);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void receive(DatagramPacket packet)
  {
    try
    {
      socket.receive(packet);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void disconnect()
  {
    socket.disconnect();
    socket = null;
  }
}