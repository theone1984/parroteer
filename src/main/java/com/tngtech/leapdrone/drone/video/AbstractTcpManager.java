package com.tngtech.leapdrone.drone.video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractTCPManager
{

  protected InetAddress inetaddr = null;

  protected Socket socket = null;

  public boolean connect(InetAddress inetaddr, int port)
  {
    try
    {
      this.inetaddr = inetaddr;
      socket = new Socket(inetaddr, port);
      socket.setSoTimeout(3000);
    } catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void close()
  {
    try
    {
      socket.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  protected void ticklePort(int port)
  {
    byte[] buf = {0x01, 0x00, 0x00, 0x00};
    try
    {
      OutputStream os = socket.getOutputStream();
      os.write(buf);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  protected InputStream getInputStream()
  {
    try
    {
      return socket.getInputStream();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
  

