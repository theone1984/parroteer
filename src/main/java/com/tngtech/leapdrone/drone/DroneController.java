package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.injection.Context;

public class DroneController
{
  public static final String DRONE_IP_ADDRESS = "192.168.1.1";

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  public static void main(String[] args)
  {
    DroneController droneController = Context.getBean(DroneController.class);
    droneController.start();
  }

  @Inject
  public DroneController(CommandSender commandSender, NavigationDataRetriever navigationDataRetriever)
  {
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
  }

  public void start()
  {
    commandSender.start();
    navigationDataRetriever.start();
  }

  public void stop()
  {
    commandSender.stop();
    navigationDataRetriever.stop();
  }

  public void takeOff()
  {
    commandSender.sendTakeOffCommand();
  }

  public void land()
  {
    commandSender.sendLandCommand();
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    //System.out.println(String.format("Got values: Roll: %.3f, Pitch: %.3f, Yaw: %.3f, Gaz: %.3f", roll, pitch, yaw, gaz));
    commandSender.move(roll, pitch, yaw, gaz);
  }
}