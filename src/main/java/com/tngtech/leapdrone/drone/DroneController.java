package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;

public class DroneController
{
  public static final String DRONE_IP_ADDRESS = "192.168.1.1";

  private final DroneCommandSender droneCommandSender;

  @Inject
  public DroneController(DroneCommandSender droneCommandSender, NavigationDataRetriever navigationDataRetriever)
  {
    this.droneCommandSender = droneCommandSender;
  }

  public void connect()
  {
    droneCommandSender.connect();
  }

  public void takeOff()
  {
    droneCommandSender.sendTakeOff();
  }

  public void land()
  {
    droneCommandSender.sendLand();
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    //System.out.println(String.format("Got values: Roll: %.3f, Pitch: %.3f, Yaw: %.3f, Gaz: %.3f", roll, pitch, yaw, gaz));
    droneCommandSender.sendWatchDogCommand();
    droneCommandSender.move(roll, pitch, yaw, gaz);
  }
}