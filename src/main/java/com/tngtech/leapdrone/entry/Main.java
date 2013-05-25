package com.tngtech.leapdrone.entry;

import com.google.inject.Inject;
import com.tngtech.leapdrone.control.DroneInputController;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.listeners.ErrorListener;
import com.tngtech.leapdrone.input.leapmotion.LeapMotionController;
import com.tngtech.leapdrone.input.speech.SpeechDetector;
import com.tngtech.leapdrone.ui.FxWindow;
import com.tngtech.leapdrone.ui.data.UIAction;
import com.tngtech.leapdrone.ui.listeners.UIActionListener;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Main implements ErrorListener, UIActionListener
{
  private final Logger logger = Logger.getLogger(ErrorListener.class);

  private final FxWindow fxWindow;

  private final DroneController droneController;

  private final SpeechDetector speechDetector;

  private final LeapMotionController leapMotionController;

  private final DroneInputController droneInputController;

  @Inject
  public Main(FxWindow fxWindow, DroneController droneController, SpeechDetector speechDetector, LeapMotionController leapMotionController,
              DroneInputController droneInputController)
  {
    this.fxWindow = fxWindow;
    this.droneController = droneController;
    this.speechDetector = speechDetector;
    this.leapMotionController = leapMotionController;
    this.droneInputController = droneInputController;
  }

  public void start(Stage primaryStage)
  {
    addEventListeners();
    startComponents(primaryStage);
  }

  private void addEventListeners()
  {
    droneController.addErrorListener(this);
    fxWindow.addUIActionListener(this);

    droneController.addVideoDataListener(fxWindow);

    droneController.addNavDataListener(droneInputController);
    leapMotionController.addDetectionListener(droneInputController);
    leapMotionController.addGestureListener(droneInputController);
    speechDetector.addSpeechListener(droneInputController);
    fxWindow.addUIActionListener(droneInputController);
  }

  private void startComponents(Stage primaryStage)
  {
    droneController.startAsync(new Config("com.tngtech.internal.leap-drone", "myProfile"));
    fxWindow.start(primaryStage);

    leapMotionController.connect();
    //speechDetector.start();
  }

  public void stop()
  {
    droneController.stop();
    leapMotionController.disconnect();
    //speechDetector.stop();
  }

  @Override
  public void onError(Throwable e)
  {
    logger.error("There was an error", e);
    System.exit(1);
  }

  @Override
  public void onAction(UIAction action)
  {
    if (action == UIAction.CLOSE_APPLICATION)
    {
      stop();
    }
  }
}