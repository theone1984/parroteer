package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.FlightModeCommand;
import com.tngtech.leapdrone.drone.commands.FlightMoveCommand;
import com.tngtech.leapdrone.drone.commands.PlayFlightAnimationCommand;
import com.tngtech.leapdrone.drone.commands.PlayLedAnimationCommand;
import com.tngtech.leapdrone.drone.commands.SetConfigValueCommand;
import com.tngtech.leapdrone.drone.commands.SwitchCameraCommand;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.drone.components.ReadyStateListenerComponent;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.enums.ControllerState;
import com.tngtech.leapdrone.drone.data.enums.DroneVersion;
import com.tngtech.leapdrone.drone.listeners.ErrorListener;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.ReadyStateChangeListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkState;

public class DroneController
{
  private static final int NUMBER_OF_THREADS = 1;

  private final Logger logger = Logger.getLogger(DroneController.class.getSimpleName());

  private final ReadyStateListenerComponent readyStateListenerComponent;

  private final ErrorListenerComponent errorListenerComponent;

  private final DroneCoordinator droneCoordinator;

  private final CommandSenderCoordinator commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetrieverP264 videoRetrieverP264;

  private final VideoRetrieverH264 videoRetrieverH264;

  private final ExecutorService executor;

  private Config config;

  @Inject
  public DroneController(ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                         DroneCoordinator droneCoordinator, CommandSenderCoordinator commandSender,
                         NavigationDataRetriever navigationDataRetriever, VideoRetrieverP264 videoRetrieverP264,
                         VideoRetrieverH264 videoRetrieverH264)
  {
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;
    this.droneCoordinator = droneCoordinator;
    this.commandSender = commandSender;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetrieverP264 = videoRetrieverP264;
    this.videoRetrieverH264 = videoRetrieverH264;
    executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
  }

  public Future startAsync(final Config config)
  {
    return executor.submit(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          start(config);
        } catch (Throwable e)
        {
          errorListenerComponent.emitError(e);
        }
      }
    });
  }

  public void start(Config config)
  {
    checkInitializationStateStarted();
    logger.info("Starting drone controller");

    this.config = config;
    droneCoordinator.start(config);
    readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
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
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void removeReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
  {
    readyStateListenerComponent.addReadyStateChangeListener(readyStateChangeListener);
  }

  public void addErrorListener(ErrorListener errorListener)
  {
    errorListenerComponent.addErrorListener(errorListener);
  }

  public void removeErrorListener(ErrorListener errorListener)
  {
    errorListenerComponent.removeErrorListener(errorListener);
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

  public Future setConfigurationValue(String key, Object value)
  {
    checkInitializationState();

    logger.debug(String.format("Setting config setting '%s' to '%s'", key, value.toString()));
    return sendAsyncConfigCommand(new SetConfigValueCommand(config.getSessionChecksum(), config.getProfileChecksum(),
            config.getApplicationChecksum(), key, value));
  }

  public Future switchCamera(SwitchCameraCommand.Camera camera)
  {
    checkInitializationState();

    logger.debug(String.format("Changing camera to '%s'", camera.name()));
    return sendAsyncConfigCommand(new SwitchCameraCommand(config.getSessionChecksum(), config.getProfileChecksum(),
            config.getApplicationChecksum(), camera));
  }

  public Future sendAsyncConfigCommand(final SetConfigValueCommand configCommand)
  {
    return executor.submit(new Runnable()
    {
      @Override
      public void run()
      {
        commandSender.sendConfigCommand(configCommand);
      }
    });
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

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    checkInitializationState();

    logger.trace(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    commandSender.sendCommand(new FlightMoveCommand(roll, pitch, yaw, gaz));
  }

  public void playLedAnimation(PlayLedAnimationCommand.LedAnimation ledAnimation, float frequency, int durationSeconds)
  {
    commandSender.sendCommand(new PlayLedAnimationCommand(config.getSessionChecksum(), config.getProfileChecksum(),
            config.getApplicationChecksum(), ledAnimation, frequency, durationSeconds));
  }

  public void playFlightAnimation(PlayFlightAnimationCommand.FlightAnimation animation)
  {
    commandSender.sendCommand(new PlayFlightAnimationCommand(config.getSessionChecksum(), config.getProfileChecksum(),
            config.getApplicationChecksum(), animation));
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