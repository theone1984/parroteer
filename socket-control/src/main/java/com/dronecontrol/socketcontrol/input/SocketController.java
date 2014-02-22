package com.dronecontrol.socketcontrol.input;

import com.dronecontrol.droneapi.data.NavData;
import com.dronecontrol.droneapi.listeners.NavDataListener;
import com.dronecontrol.socketcontrol.config.Config;
import com.dronecontrol.socketcontrol.input.events.MovementDataListener;
import com.dronecontrol.socketcontrol.input.events.PilotActionListener;
import com.dronecontrol.socketcontrol.input.socket.SocketClient;

import javax.inject.Inject;

public class SocketController implements NavDataListener
{
  private final SocketClient socketClient;

  private final SocketDataReceiver socketDataReceiver;

  private final SocketDataSender socketDataSender;

  private final String hostName;

  private final Integer port;

  @Inject
  public SocketController(Config config, SocketClient socketClient, SocketDataReceiver socketDataReceiver, SocketDataSender socketDataSender)
  {
    this.socketClient = socketClient;
    this.socketDataReceiver = socketDataReceiver;
    this.socketDataSender = socketDataSender;

    hostName = config.getHostName();
    port = config.getPort();
  }

  public void addMovementDataListener(MovementDataListener listener)
  {
    socketDataReceiver.addMovementDataListener(listener);
  }

  public void addPilotActionListener(PilotActionListener listener)
  {
    socketDataReceiver.addPilotActionListener(listener);
  }

  public void start()
  {
    socketClient.connect(hostName, port);
    socketClient.addDataListener(socketDataReceiver);
  }

  public void stop()
  {
    socketClient.removeDataListener(socketDataReceiver);
    socketClient.disconnect();

    socketDataReceiver.dispose();
  }


  @Override
  public void onNavData(NavData navData)
  {
    socketDataSender.onNavData(navData);
  }
}