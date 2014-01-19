package com.dronecontrol.kinectcontrol.input;

import com.dronecontrol.kinectcontrol.input.events.MovementDataListener;
import com.dronecontrol.kinectcontrol.input.socket.SocketClient;

import javax.inject.Inject;

public class KinectController
{
  private static final String HOST_NAME = "localhost";

  private static final int HOST_PORT = 4500;

  private final SocketClient socketClient;

  private final KinectDataReceiver kinectDataReceiver;

  @Inject
  public KinectController(SocketClient socketClient, KinectDataReceiver kinectDataReceiver)
  {
    this.socketClient = socketClient;
    this.kinectDataReceiver = kinectDataReceiver;
  }

  public void addMovementDataListener(MovementDataListener droneInputController)
  {
    kinectDataReceiver.addMovementDataListener(droneInputController);
  }

  public void start()
  {
    socketClient.start(HOST_NAME, HOST_PORT);
    socketClient.addDataListener(kinectDataReceiver);
  }

  public void stop()
  {
    socketClient.removeDataListener(kinectDataReceiver);
    socketClient.close();
  }
}