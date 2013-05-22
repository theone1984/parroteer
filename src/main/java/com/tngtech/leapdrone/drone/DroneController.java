package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.FlightModeCommand;
import com.tngtech.leapdrone.drone.commands.FlightMoveCommand;
import com.tngtech.leapdrone.drone.config.DroneControllerConfig;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.helpers.components.AddressComponent;
import com.tngtech.leapdrone.injection.Context;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;


@SuppressWarnings("UnusedDeclaration")
public class DroneController
{
  private final Logger logger = Logger.getLogger(DroneController.class.getSimpleName());

  private final CommandSender commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final DroneCoordinator droneCoordinator;

  private final VideoRetrieverAbstract videoRetriever;

  private final AddressComponent addressComponent;

  public static void main(String[] args)
  {
    DroneController droneController = Context.getBean(DroneController.class);
    droneController.start();
  }

  @Inject
  public DroneController(CommandSender commandSender, NavigationDataRetriever navigationDataRetriever,
                         ArDroneOneVideoRetriever arDroneOnevideoRetriever, ArDroneTwoVideoRetriever arDroneTwoVideoRetriever,
                         AddressComponent addressComponent, DroneCoordinator droneCoordinator)
  {
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.droneCoordinator = droneCoordinator;
    this.videoRetriever = DroneControllerConfig.DRONE_VERSION == DroneControllerConfig.DroneVersion.ARDRONE_1 ? arDroneOnevideoRetriever : arDroneTwoVideoRetriever;
    this.addressComponent = addressComponent;
  }

  public void start()
  {
    checkIfDroneIsReachable();

    logger.info("Starting drone controller");
    droneCoordinator.start();
  }

  private void checkIfDroneIsReachable()
  {
    checkState(addressComponent.isReachable(DroneControllerConfig.DRONE_IP_ADDRESS, DroneControllerConfig.REACHABLE_TIMEOUT), "The drone could not be pinged");
    logger.info("The drone could be pinged");
  }

  public void stop()
  {
    logger.info("Stopping drone controller");
    droneCoordinator.stop();
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
    logger.debug("Taking off");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.TAKE_OFF));
  }

  public void land()
  {
    logger.debug("Landing");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.LAND));
  }

  public void emergency()
  {
    logger.debug("Setting emergency");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.EMERGENCY));
  }

  public void flatTrim()
  {
    logger.debug("Flat trim");
    commandSender.sendCommand(new FlatTrimCommand());
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    logger.trace(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    commandSender.sendCommand(new FlightMoveCommand(roll, pitch, yaw, gaz));
  }
}