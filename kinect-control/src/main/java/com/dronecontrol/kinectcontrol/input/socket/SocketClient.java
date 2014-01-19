package com.dronecontrol.kinectcontrol.input.socket;

import com.google.common.collect.Sets;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Set;

public class SocketClient implements IDataHandler
{
  private static final String LINE_TERMINATION_STRING = "\n";

  private static final String LINE_TERMINATION_PATTERN = "\\n";

  private INonBlockingConnection socketConnection;

  private Set<SocketClientDataListener> dataListeners;

  public SocketClient()
  {
    dataListeners = Sets.newCopyOnWriteArraySet();
  }

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
    String content = nbc.readStringByDelimiter(LINE_TERMINATION_STRING);
    String[] messages = content.split(LINE_TERMINATION_PATTERN);

    invokeCallbacksForMessages(messages);
    return true;
  }

  private void invokeCallbacksForMessages(String[] messages)
  {
    for (String message : messages)
    {
      for (SocketClientDataListener callback : dataListeners)
      {
        callback.OnData(message);
      }
    }
  }

  @SuppressWarnings("UnusedDeclaration")
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

  public synchronized void addDataListener(SocketClientDataListener callback)
  {
    dataListeners.add(callback);
  }

  public synchronized void removeDataListener(SocketClientDataListener callback)
  {
    dataListeners.remove(callback);
  }
}