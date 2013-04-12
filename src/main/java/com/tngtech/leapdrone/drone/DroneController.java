package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;

public class DroneController
{
  private final DroneCommunicator droneCommunicator;

  @Inject
  public DroneController(DroneCommunicator droneCommunicator)
  {
    this.droneCommunicator = droneCommunicator;
  }

  public void connect()
  {
    droneCommunicator.connect();
  }

  public void takeOff()
  {
    droneCommunicator.sendTakeOff();
  }

  public void land()
  {
    droneCommunicator.sendLand();
  }
}