package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.FlightModeCommand;
import com.tngtech.leapdrone.drone.commands.FlightMoveCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.injection.Context;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;


@SuppressWarnings("UnusedDeclaration")
public class DroneController
{
  private final Logger logger = Logger.getLogger(DroneController.class.getSimpleName());

  private final DroneCoordinator droneCoordinator;

  public static void main(String[] args)
  {
    DroneController droneController = Context.getBean(DroneController.class);
    droneController.start();
  }

  @Inject
  public DroneController(DroneCoordinator droneCoordinator)
  {
    this.droneCoordinator = droneCoordinator;
  }

  public void start()
  {
    checkInitializationStateStarted();
    logger.info("Starting drone controller");
    droneCoordinator.start();
  }

  public void stop()
  {
    checkInitializationState();
    logger.info("Stopping drone controller");
    droneCoordinator.stop();
  }

  public boolean isInitialized()
  {
    return droneCoordinator.getState() == DroneCoordinator.State.READY;
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    droneCoordinator.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    droneCoordinator.removeReadyStateChangeListener(readyStateChangeListener);
  }

  public void addNavDataListener(NavDataListener navDataListener)
  {
    droneCoordinator.getNavigationDataRetriever().addNavDataListener(navDataListener);
  }

  public void removeNavDataListener(NavDataListener navDataListener)
  {
    droneCoordinator.getNavigationDataRetriever().removeNavDataListener(navDataListener);
  }

  public void addVideoDataListener(VideoDataListener videoDataListener)
  {
    droneCoordinator.getVideoRetriever().addVideoDataListener(videoDataListener);
  }

  public void removeVideoDataListener(VideoDataListener videoDataListener)
  {
    droneCoordinator.getVideoRetriever().removeVideoDataListener(videoDataListener);
  }

  public DroneConfiguration getDroneConfiguration()
  {
    checkInitializationState();

    return droneCoordinator.getDroneConfiguration();
  }

  public void takeOff()
  {
    checkInitializationState();

    logger.debug("Taking off");
    droneCoordinator.getCommandSender().sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.TAKE_OFF));
  }

  public void land()
  {
    checkInitializationState();

    logger.debug("Landing");
    droneCoordinator.getCommandSender().sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.LAND));
  }

  public void emergency()
  {
    checkInitializationState();

    logger.debug("Setting emergency");
    droneCoordinator.getCommandSender().sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.EMERGENCY));
  }

  public void flatTrim()
  {
    checkInitializationState();

    logger.debug("Flat trim");
    droneCoordinator.getCommandSender().sendCommand(new FlatTrimCommand());
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    checkInitializationState();

    logger.trace(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    droneCoordinator.getCommandSender().sendCommand(new FlightMoveCommand(roll, pitch, yaw, gaz));
  }

  private void checkInitializationState()
  {
    checkState(isInitialized(), "The drone controller is not yet fully initialized");
  }

  private void checkInitializationStateStarted()
  {
    checkState(droneCoordinator.getState() == DroneCoordinator.State.STARTED, "The drone controller has already been initialized");
  }
}