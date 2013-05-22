package com.tngtech.leapdrone.helpers.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TcpComponent
{
  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  private static final int DEFAULT_TIMEOUT = 3000;

  private Socket socket = null;

  private BufferedReader reader = null;

  public void connect(InetAddress address, int port)
  {
    connect(address, port, DEFAULT_TIMEOUT);
  }

  public void connect(InetAddress address, int port, int timeout)
  {
    try
    {
      socket = new Socket(address, port);
      socket.setSoTimeout(timeout);
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException e)
    {
      throw new IllegalStateException(String.format("Error while connecting to TCP socket %s:%d", address.getHostName(), port), e);
    }
  }

  public void disconnect()
  {
    try
    {
      socket.close();

      socket = null;
      reader = null;
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while disconnecting socket", e);
    }
  }

  public void sendKeepAlivePacket()
  {
    try
    {
      socket.getOutputStream().write(KEEP_ALIVE_BYTES);
    } catch (IOException e)
    {
      throw new IllegalStateException("Error sending keep alive packet bytes", e);
    }
  }

  public InputStream getInputStream()
  {
    try
    {
      return socket.getInputStream();
    } catch (IOException e)
    {
      throw new IllegalStateException("Error getting input stream", e);
    }
  }

  public BufferedReader getReader()
  {
    return reader;
  }
}