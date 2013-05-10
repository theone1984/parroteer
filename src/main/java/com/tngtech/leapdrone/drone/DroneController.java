package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.AddressComponent;
import com.tngtech.leapdrone.drone.config.DroneConfig;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.injection.Context;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;


public class DroneController
{
  private final Logger logger = Logger.getLogger(DroneController.class.getSimpleName());

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetriever videoRetriever;

  private final AddressComponent addressComponent;

  public static void main(String[] args)
  {
    DroneController droneController = Context.getBean(DroneController.class);
    droneController.start();
  }

  @Inject
  public DroneController(CommandSender commandSender, NavigationDataRetriever navigationDataRetriever, VideoRetriever videoRetriever,
                         AddressComponent addressComponent)
  {
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetriever = videoRetriever;
    this.addressComponent = addressComponent;
  }

  public void start()
  {
    checkIfDroneIsReachable();

    logger.info("Starting dronce controller");
    //commandSender.start();
    //navigationDataRetriever.start();
    videoRetriever.start();
  }

  private void checkIfDroneIsReachable()
  {
    checkState(addressComponent.isReachable(DroneConfig.DRONE_IP_ADDRESS, DroneConfig.REACHABLE_TIMEOUT), "The drone could not be pinged");
  }

  public void stop()
  {
    logger.info("Stopping dronce controller");
    //commandSender.stop();
    //navigationDataRetriever.stop();
    videoRetriever.stop();
  }

  public void addNavDataListener(NavDataListener navDataListener)
  {
    navigationDataRetriever.addNavDataListener(navDataListener);
  }

  public void removeNavDataListener(NavDataListener navDataListener)
  {
    navigationDataRetriever.removeNavDataListener(navDataListener);
  }

  public void addVideoDataListener(VideoDataListener videoDataListener)
  {
    videoRetriever.addVideoDataListener(videoDataListener);
  }

  public void removeVideoDataListener(VideoDataListener videoDataListener)
  {
    videoRetriever.removeVideoDataListener(videoDataListener);
  }

  public void takeOff()
  {
    commandSender.sendTakeOffCommand();
  }

  public void land()
  {
    commandSender.sendLandCommand();
  }

  public void emergency()
  {
    commandSender.sendEmergencyCommand();
  }

  public void flatTrim()
  {
    commandSender.sendFlatTrimCommand();
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    commandSender.move(roll, pitch, yaw, gaz);
  }
}