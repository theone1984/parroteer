package com.dronecontrol.droneapi.components;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpComponent
{
  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  private DatagramPacket keepAlivePacket;

  private DatagramSocket socket;

  private InetAddress address;

  private int port;

  public void connect(InetAddress address, int port)
  {
    this.address = address;
    this.port = port;

    determineKeepAlivePacket(address, port);

    try
    {
      socket = new DatagramSocket(port);
      socket.setSoTimeout(3000);
    } catch (SocketException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void disconnect()
  {
    socket.disconnect();
    socket = null;
  }

  public void reconnect()
  {
    disconnect();
    connect(address, port);
  }

  private void determineKeepAlivePacket(InetAddress address, int port)
  {
    keepAlivePacket = new DatagramPacket(KEEP_ALIVE_BYTES, KEEP_ALIVE_BYTES.length, address, port);
  }

  public void sendKeepAlivePacket()
  {
    send(keepAlivePacket);
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
}