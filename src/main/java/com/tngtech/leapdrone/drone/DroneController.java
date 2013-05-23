package com.tngtech.leapdrone.drone;


import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.FlightModeCommand;
import com.tngtech.leapdrone.drone.commands.FlightMoveCommand;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.commands.SwitchCameraCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.enums.ControllerState;
import com.tngtech.leapdrone.drone.data.enums.DroneVersion;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.helpers.components.ReadyStateComponent;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("UnusedDeclaration")
public class DroneController
{
  private final Logger logger = Logger.getLogger(DroneController.class.getSimpleName());

  private final ReadyStateComponent readyStateComponent;

  private final DroneCoordinator droneCoordinator;

  private final CommandSenderCoordinator commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetrieverP264 videoRetrieverP264;

  private final VideoRetrieverH264 videoRetrieverH264;

  @Inject
  public DroneController(ReadyStateComponent readyStateComponent, DroneCoordinator droneCoordinator, CommandSenderCoordinator commandSender,
                         NavigationDataRetriever navigationDataRetriever, VideoRetrieverP264 videoRetrieverP264,
                         VideoRetrieverH264 videoRetrieverH264)
  {
    this.readyStateComponent = readyStateComponent;
    this.droneCoordinator = droneCoordinator;
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetrieverP264 = videoRetrieverP264;
    this.videoRetrieverH264 = videoRetrieverH264;
  }

  public void start()
  {
    checkInitializationStateStarted();
    logger.info("Starting drone controller");

    droneCoordinator.start();
    readyStateComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);

    System.out.println(droneCoordinator.getDroneConfiguration().getConfig().get(DroneConfiguration.PROFILE_ID_KEY));
  }

  public void stop()
  {
    checkInitializationState();
    logger.info("Stopping drone controller");
    droneCoordinator.stop();
  }

  public boolean isInitialized()
  {
    return droneCoordinator.getState() == ControllerState.READY;
  }

  public void addReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateComponent.addReadyStateChangeListener(readyStateChangeListener);
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
    videoRetrieverH264.addVideoDataListener(videoDataListener);
    videoRetrieverP264.addVideoDataListener(videoDataListener);
  }

  public void removeVideoDataListener(VideoDataListener videoDataListener)
  {
    videoRetrieverH264.removeVideoDataListener(videoDataListener);
    videoRetrieverP264.removeVideoDataListener(videoDataListener);
  }

  public DroneVersion getDroneVersion()
  {
    checkInitializationState();
    return droneCoordinator.getDroneVersion();
  }

  public DroneConfiguration getDroneConfiguration()
  {
    checkInitializationState();
    return droneCoordinator.getDroneConfiguration();
  }

  /**
   * CAUTION: This is a synchronous method. Don't call it from the nav data or drone configuration callback methods!
   */
  public void setConfigurationValue(String key, Object value)
  {
    checkInitializationState();

    logger.debug(String.format("Setting config setting '%s' to '%s'", key, value.toString()));
    commandSender.sendConfigCommand(new SetConfigValueCommand(key, value));
  }

  public void takeOff()
  {
    checkInitializationState();

    logger.debug("Taking off");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.TAKE_OFF));
  }

  public void land()
  {
    checkInitializationState();

    logger.debug("Landing");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.LAND));
  }

  public void emergency()
  {
    checkInitializationState();

    logger.debug("Setting emergency");
    commandSender.sendCommand(new FlightModeCommand(FlightModeCommand.FlightMode.EMERGENCY));
  }

  public void flatTrim()
  {
    checkInitializationState();

    logger.debug("Flat trim");
    commandSender.sendCommand(new FlatTrimCommand());
  }

  public void switchCamera(SwitchCameraCommand.Camera camera)
  {
    checkInitializationState();

    logger.debug(String.format("Changing camera to '%s'", camera.name()));
    // Since this is a config command changed very often, we don't need to reload the config here
    commandSender.sendConfigCommand(new SwitchCameraCommand(camera));
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    checkInitializationState();

    logger.trace(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    commandSender.sendCommand(new FlightMoveCommand(roll, pitch, yaw, gaz));
  }

  private void checkInitializationState()
  {
    checkState(isInitialized(), "The drone controller is not yet fully initialized");
  }

  private void checkInitializationStateStarted()
  {
    checkState(droneCoordinator.getState() == ControllerState.STARTED, "The drone controller has already been initialized");
  }
}