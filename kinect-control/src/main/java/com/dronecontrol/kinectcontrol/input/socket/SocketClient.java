package com.dronecontrol.kinectcontrol.input.socket;

import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

public class SocketClient implements IDataHandler
{
  private INonBlockingConnection socketConnection;

  public void start(String hostName, int port)
  {
    try
    {
      socketConnection = new NonBlockingConnection(hostName, port, this);
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while connecting to the socket server", e);
    }
  }

  @Override
  public boolean onData(INonBlockingConnection nbc) throws IOException, BufferUnderflowException
  {
    String content = nbc.readStringByDelimiter("\r\n");
    System.out.println(content);

    return true;
  }

  public void send(String content)
  {
    try
    {
      socketConnection.write(content);
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while writing to the socket server", e);
    }
  }

  public void close()
  {
    try
    {
      socketConnection.close();
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while disconnecting from the socket server", e);
    }
  }
}