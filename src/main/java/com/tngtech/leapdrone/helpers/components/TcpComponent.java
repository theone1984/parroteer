package com.tngtech.leapdrone.helpers.components;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TcpComponent
{
  private static final byte[] KEEP_ALIVE_BYTES = new byte[]{0x01, 0x00, 0x00, 0x00};

  private Socket socket = null;

  public void connect(InetAddress address, int port)
  {
    try
    {
      socket = new Socket(address, port);
      socket.setSoTimeout(3000);
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
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while disconnecting socket", e);
    }
  }

  public void sendKeepAlivePacket()
  {
    try
    {
      OutputStream os = socket.getOutputStream();
      os.write(KEEP_ALIVE_BYTES);
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
}