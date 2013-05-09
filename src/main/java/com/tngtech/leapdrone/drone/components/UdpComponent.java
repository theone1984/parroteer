package com.tngtech.leapdrone.drone.components;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpComponent
{
  private DatagramSocket socket;

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