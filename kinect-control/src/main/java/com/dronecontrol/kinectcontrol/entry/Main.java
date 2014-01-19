package com.dronecontrol.kinectcontrol.entry;

import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.listeners.ErrorListener;
import com.dronecontrol.kinectcontrol.control.DroneInputController;
import com.dronecontrol.kinectcontrol.input.KinectController;
import com.dronecontrol.kinectcontrol.ui.FxController;
import com.dronecontrol.kinectcontrol.ui.FxWindow;
import com.dronecontrol.kinectcontrol.ui.data.UIAction;
import com.dronecontrol.kinectcontrol.ui.listeners.UIActionListener;
import com.google.inject.Inject;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Main implements ErrorListener, UIActionListener
{
  private final Logger logger = Logger.getLogger(ErrorListener.class);

  private final FxWindow fxWindow;

  private final DroneController droneController;

  private final KinectController kinectController;

  private final DroneInputController droneInputController;

  private FxController fxController;

  @Inject
  public Main(FxWindow fxWindow, DroneController droneController, KinectController kinectController,
              DroneInputController droneInputController)
  {
    this.fxWindow = fxWindow;
    this.droneController = droneController;
    this.kinectController = kinectController;
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
    kinectController.addMovementDataListener(droneInputController);
    kinectController.addMovementDataListener(fxController);

    fxController.addUIActionListener(droneInputController);
  }

  private void startComponents()
  {
    //droneController.startAsync(new Config("com.dronecontrol.leap-drone", "myProfile", 2));
    kinectController.start();
  }

  public void stop()
  {
    //droneController.stop();
    kinectController.stop();

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