package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.injection.Context;

public class DroneController
{
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

  public void addNavDataListener(NavDataListener navDataListener)
  {
    navigationDataRetriever.addNavDataListener(navDataListener);
  }

  public void removeNavDataListener(NavDataListener navDataListener)
  {
    navigationDataRetriever.removeNavDataListener(navDataListener);
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
    commandSender.move(roll, pitch, yaw, gaz);
  }
}