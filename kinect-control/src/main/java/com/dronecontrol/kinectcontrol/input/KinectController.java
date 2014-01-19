package com.dronecontrol.kinectcontrol.input;

import com.dronecontrol.kinectcontrol.config.Config;
import com.dronecontrol.kinectcontrol.input.events.MovementDataListener;
import com.dronecontrol.kinectcontrol.input.events.PilotActionListener;
import com.dronecontrol.kinectcontrol.input.socket.SocketClient;

import javax.inject.Inject;

public class KinectController
{
  private final SocketClient socketClient;

  private final KinectDataReceiver kinectDataReceiver;

  private final String hostName;

  private final Integer port;

  @Inject
  public KinectController(Config config, SocketClient socketClient, KinectDataReceiver kinectDataReceiver)
  {
    this.socketClient = socketClient;
    this.kinectDataReceiver = kinectDataReceiver;

    hostName = config.getHostName();
    port = config.getPort();
  }

  public void addMovementDataListener(MovementDataListener listener)
  {
    kinectDataReceiver.addMovementDataListener(listener);
  }

  public void addPilotActionListener(PilotActionListener listener)
  {
    kinectDataReceiver.addPilotActionListener(listener);
  }

  public void start()
  {
    socketClient.connect(hostName, port);
    socketClient.addDataListener(kinectDataReceiver);
  }

  public void stop()
  {
    socketClient.removeDataListener(kinectDataReceiver);
    socketClient.disconnect();

    kinectDataReceiver.dispose();
  }
}