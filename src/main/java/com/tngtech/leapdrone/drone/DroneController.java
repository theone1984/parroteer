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
    //droneCommunicator.connect();
  }

  public void takeOff()
  {
    droneCommunicator.sendTakeOff();
  }

  public void land()
  {
    droneCommunicator.sendLand();
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    System.out.println(String.format("Got values: Roll: %.3f, Pitch: %.3f, Yaw: %.3f, Gaz: %.3f", roll, pitch, yaw, gaz));
    //droneCommunicator.move(roll, pitch, yaw, gaz);
  }
}