package com.dronecontrol.leapcontrol.entry;

import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.data.Config;
import com.dronecontrol.droneapi.listeners.ErrorListener;
import com.dronecontrol.leapcontrol.control.DroneInputController;
import com.dronecontrol.leapcontrol.input.leapmotion.LeapMotionController;
import com.dronecontrol.leapcontrol.input.speech.SpeechDetector;
import com.dronecontrol.leapcontrol.ui.FxController;
import com.dronecontrol.leapcontrol.ui.FxWindow;
import com.dronecontrol.leapcontrol.ui.data.UIAction;
import com.dronecontrol.leapcontrol.ui.listeners.UIActionListener;
import com.google.inject.Inject;
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

  private FxController fxController;

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
    startWindow(primaryStage);

    addEventListeners();
    startComponents();
  }

  private void startWindow(Stage primaryStage)
  {
    fxController = fxWindow.start(primaryStage);
  }

  private void addEventListeners()
  {
    droneController.addErrorListener(this);
    fxController.addUIActionListener(this);

    droneController.addVideoDataListener(fxController);
    droneController.addNavDataListener(fxController);

    droneController.addNavDataListener(droneInputController);
    droneController.addReadyStateChangeListener(droneInputController);
    leapMotionController.addDetectionListener(droneInputController);
    leapMotionController.addGestureListener(droneInputController);
    speechDetector.addSpeechListener(droneInputController);
    fxController.addUIActionListener(droneInputController);
  }

  private void startComponents()
  {
    droneController.startAsync(new Config("com.dronecontrol.leap-drone", "myProfile", 2));
    leapMotionController.connect();
    //speechDetector.start();
  }

  public void stop()
  {
    droneController.stop();
    leapMotionController.disconnect();
    //speechDetector.stop();

    System.exit(0);
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