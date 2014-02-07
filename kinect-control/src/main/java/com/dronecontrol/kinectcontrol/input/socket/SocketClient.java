package com.dronecontrol.kinectcontrol.input.socket;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

public class SocketClient implements IConnectHandler, IDisconnectHandler, IDataHandler
{
  private static final int RECONNECT_TIMEOUT_SECONDS = 5;

  private static final String LINE_TERMINATION_STRING = "\n";

  private static final String LINE_TERMINATION_PATTERN = "\\n";

  private final Logger logger = Logger.getLogger(SocketClient.class);

  private final ScheduledExecutorService worker;

  private final Set<SocketClientDataListener> dataListeners;

  private INonBlockingConnection socketConnection;

  private String hostName;

  private int port;

  private boolean connected;

  public SocketClient()
  {
    worker = Executors.newSingleThreadScheduledExecutor();
    dataListeners = Sets.newCopyOnWriteArraySet();
  }

  public void connect(String hostName, int port)
  {
    checkState(!connected, "The socket client is already connected");

    this.hostName = hostName;
    this.port = port;
    connected = false;

    startConnecting();
  }

  private void startConnecting()
  {
    if (connected)
    {
      return;
    }

    try
    {
      logger.info(String.format("Connecting to socket server at %s:%d", hostName, port));
      socketConnection = new NonBlockingConnection(hostName, port, this);
    } catch (IOException e)
    {
      logger.info(String.format("Connection to socket server failed - retry in %d seconds", RECONNECT_TIMEOUT_SECONDS));
      tryReconnect();
    }
  }

  public void tryReconnect()
  {
    Runnable task = new Runnable()
    {
      public void run()
      {
        startConnecting();
      }
    };
    worker.schedule(task, RECONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
  }

  @Override
  public boolean onConnect(INonBlockingConnection iNonBlockingConnection) throws IOException, BufferUnderflowException
  {
    logger.info("Connection to socket server established");
    connected = true;
    return true;
  }

  @Override
  public boolean onDisconnect(INonBlockingConnection iNonBlockingConnection) throws IOException
  {
    logger.info(String.format("Connection to socket server disconnected - retrying connect in %d seconds", RECONNECT_TIMEOUT_SECONDS));
    connected = false;

    tryReconnect();
    return true;
  }

  @Override
  public boolean onData(INonBlockingConnection connection)
  {
    String[] messages = getMessagesFromConnection(connection);
    if (messages != null)
    {
      invokeCallbacksForMessages(messages);
    }
    return true;
  }

  private String[] getMessagesFromConnection(INonBlockingConnection connection)
  {
    String[] messages = null;
    try
    {
      String content = connection.readStringByDelimiter(LINE_TERMINATION_STRING);
      messages = content.split(LINE_TERMINATION_PATTERN);
    } catch (Exception e)
    {
      logger.warn(String.format("Receiving data failed: %s", e.getMessage()));
    }
    return messages;
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
      socketConnection.write(content + LINE_TERMINATION_STRING);
    } catch (IOException e)
    {
      logger.warn(String.format("Sending data failed: %s", e.getMessage()));
    }
  }

  public void disconnect()
  {
    try
    {
      if (!connected)
      {
        return;
      }
      socketConnection.close();
    } catch (IOException e)
    {
      logger.warn(String.format("Disconnecting from socket server failed: %s", e.getMessage()));
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

  public boolean isConnected()
  {
    return connected;
  }
}