package com.tngtech.leapdrone.drone;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class NavigationDataRetriever
{
  DatagramSocket socket_nav;

  InetAddress inet_addr;

  DatagramSocket socket_at;

  static final int AT_PORT = 5556;

  static final int NAVDATA_PORT = 5554;

  static final int NAVDATA_BATTERY = 24;

  static final int NAVDATA_ALTITUDE = 40;

  public static void main(String[] args)
  {
    try
    {
      new NavigationDataRetriever().run();
    } catch (SocketException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (UnknownHostException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void run() throws SocketException, UnknownHostException
  {
    socket_at = new DatagramSocket(AT_PORT);
    socket_at.setSoTimeout(3000);

    inet_addr = InetAddress.getByName("192.168.1.1");
    socket_nav = new DatagramSocket(NAVDATA_PORT);
    socket_nav.setSoTimeout(3000);

    int cnt = 0;

    try
    {
      byte[] buf_snd = {0x01, 0x00, 0x00, 0x00};
      DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, NAVDATA_PORT);
      socket_nav.send(packet_snd);

      send_at_cmd("AT*CONFIG=1,\"general:navdata_demo\",\"TRUE\"");

      byte[] buf_rcv = new byte[10240];
      DatagramPacket packet_rcv = new DatagramPacket(buf_rcv, buf_rcv.length);

      while (true)
      {
        try
        {
          socket_nav.receive(packet_rcv);

          cnt++;
          if (cnt >= 5)
          {
            cnt = 0;
            System.out.println("NavData Received: " + packet_rcv.getLength() + " bytes");
            System.out.println("Battery: " + get_int(buf_rcv, NAVDATA_BATTERY)
                    + "%, Altitude: " + ((float) get_int(buf_rcv, NAVDATA_ALTITUDE) / 1000) + "m");

            socket_nav.send(packet_snd);
          }
        } catch (SocketTimeoutException ex3)
        {
          System.out.println("socket_nav.receive(): Timeout");
        } catch (Exception ex1)
        {
          ex1.printStackTrace();
        }
      }
    } catch (Exception ex2)
    {
      ex2.printStackTrace();
    }
  }


  public synchronized void send_at_cmd(String at_cmd) throws Exception
  {
    byte[] buf_snd = (at_cmd + "\r").getBytes();
    DatagramPacket packet_snd = new DatagramPacket(buf_snd, buf_snd.length, inet_addr, AT_PORT);
    socket_at.send(packet_snd);
  }

  public int get_int(byte[] data, int offset)
  {
    int tmp = 0, n = 0;

    System.out.println("get_int(): data = " + byte2hex(data, offset, 4));
    for (int i = 3; i >= 0; i--)
    {
      n <<= 8;
      tmp = data[offset + i] & 0xFF;
      n |= tmp;
    }

    return n;
  }

  public String byte2hex(byte[] data, int offset, int len)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < len; i++)
    {
      String tmp = Integer.toHexString(((int) data[offset + i]) & 0xFF);
      for (int t = tmp.length(); t < 2; t++)
      {
        sb.append("0");
      }
      sb.append(tmp);
      sb.append(" ");
    }
    return sb.toString();
  }
}