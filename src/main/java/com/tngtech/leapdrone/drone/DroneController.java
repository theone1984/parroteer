package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.composed.PlayFlightAnimationCommand;
import com.tngtech.leapdrone.drone.commands.composed.PlayLedAnimationCommand;
import com.tngtech.leapdrone.drone.commands.composed.SwitchCameraCommand;
import com.tngtech.leapdrone.drone.commands.simple.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.simple.FlightModeCommand;
import com.tngtech.leapdrone.drone.commands.simple.FlightMoveCommand;
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

  private final DroneStartupCoordinator droneStartupCoordinator;

  private final CommandSenderCoordinator commandSender;

  private final NavigationDataRetriever navigationDataRetriever;

  private final VideoRetrieverP264 videoRetrieverP264;

  private final VideoRetrieverH264 videoRetrieverH264;

  private ExecutorService executor;

  private Config config;

  @Inject
  public DroneController(ReadyStateListenerComponent readyStateListenerComponent, ErrorListenerComponent errorListenerComponent,
                         DroneStartupCoordinator droneStartupCoordinator, CommandSenderCoordinator commandSenderCoordinator,
                         NavigationDataRetriever navigationDataRetriever, VideoRetrieverP264 videoRetrieverP264,
                         VideoRetrieverH264 videoRetrieverH264)
  {
    this.readyStateListenerComponent = readyStateListenerComponent;
    this.errorListenerComponent = errorListenerComponent;
    this.droneStartupCoordinator = droneStartupCoordinator;
    this.commandSender = commandSenderCoordinator;
    this.navigationDataRetriever = navigationDataRetriever;
    this.videoRetrieverP264 = videoRetrieverP264;
    this.videoRetrieverH264 = videoRetrieverH264;

  }

  public void startAsync(final Config config)
  {
    checkInitializationStateStarted();
    initializeExecutor();

    executor.submit(new Runnable()
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

    initializeExecutor();
    droneStartupCoordinator.start(config);
    readyStateListenerComponent.emitReadyStateChange(ReadyStateChangeListener.ReadyState.READY);
  }

  private void initializeExecutor()
  {
    executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
  }

  public void stop()
  {
    logger.info("Stopping drone controller");
    droneStartupCoordinator.stop();
    executor.shutdownNow();
  }

  public boolean isInitialized()
  {
    return droneStartupCoordinator.getState() == ControllerState.READY;
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
    return droneStartupCoordinator.getDroneVersion();
  }

  public DroneConfiguration getDroneConfiguration()
  {
    checkInitializationState();
    return droneStartupCoordinator.getDroneConfiguration();
  }

  public void takeOff()
  {
    checkInitializationState();

    logger.debug("Taking off");
    executeCommand(new FlightModeCommand(FlightModeCommand.FlightMode.TAKE_OFF));
  }

  public void land()
  {
    checkInitializationState();

    logger.debug("Landing");
    executeCommand(new FlightModeCommand(FlightModeCommand.FlightMode.LAND));
  }

  public void emergency()
  {
    checkInitializationState();

    logger.debug("Setting emergency");
    executeCommand(new FlightModeCommand(FlightModeCommand.FlightMode.EMERGENCY));
  }

  public void flatTrim()
  {
    checkInitializationState();

    logger.debug("Flat trim");
    executeCommand(new FlatTrimCommand());
  }

  public void move(float roll, float pitch, float yaw, float gaz)
  {
    checkInitializationState();

    logger.trace(String.format("Moving - roll: %.2f, pitch: %.2f, yaw: %.2f, gaz: %.2f", roll, pitch, yaw, gaz));
    executeCommand(new FlightMoveCommand(roll, pitch, yaw, gaz));
  }

  public Future switchCamera(SwitchCameraCommand.Camera camera)
  {
    checkInitializationState();

    logger.debug(String.format("Changing camera to '%s'", camera.name()));
    return executeCommandAsync(new SwitchCameraCommand(config.getLoginData(), camera));
  }

  public Future playLedAnimation(PlayLedAnimationCommand.LedAnimation ledAnimation, float frequency, int durationSeconds)
  {
    checkInitializationState();

    logger.debug(String.format("Playing LED animation '%s'", ledAnimation.name()));
    return executeCommandAsync(new PlayLedAnimationCommand(config.getLoginData(), ledAnimation, frequency, durationSeconds));
  }

  public Future playFlightAnimation(PlayFlightAnimationCommand.FlightAnimation animation)
  {
    checkInitializationState();

    logger.debug(String.format("Playing flight animation '%s'", animation.name()));
    return executeCommandAsync(new PlayFlightAnimationCommand(config.getLoginData(), animation));
  }

  public void executeCommand(Command command)
  {
    commandSender.executeCommand(command);
  }

  public Future executeCommandAsync(final Command command)
  {
    return executor.submit(new Runnable()
    {
      @Override
      public void run()
      {
        commandSender.executeCommand(command);
      }
    });
  }

  private void checkInitializationState()
  {
    checkState(isInitialized(), "The drone controller is not yet fully initialized");
  }

  private void checkInitializationStateStarted()
  {
    checkState(droneStartupCoordinator.getState() == ControllerState.STARTED, "The drone controller has already been initialized");
  }
}