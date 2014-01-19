package com.dronecontrol.kinectcontrol.input;

import com.dronecontrol.kinectcontrol.control.DroneInputController;
import com.dronecontrol.kinectcontrol.input.socket.SocketClient;

public class KinectController
{
  private static final String HOST_NAME = "localhost";

  private static final int HOST_PORT = 4500;

  private final SocketClient socketClient;

  public KinectController(SocketClient socketClient)
  {
    this.socketClient = socketClient;
  }

  public void addMotionListener(DroneInputController droneInputController)
  {
  }

  public void start()
  {
    socketClient.start(HOST_NAME, HOST_PORT);
  }

  public void stop()
  {
  }
}