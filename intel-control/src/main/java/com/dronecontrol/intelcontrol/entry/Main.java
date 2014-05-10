package com.dronecontrol.intelcontrol.entry;

import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.data.Config;
import com.dronecontrol.droneapi.listeners.ErrorListener;
import com.dronecontrol.intelcontrol.control.DroneInputController;
import com.dronecontrol.intelcontrol.ui.FxController;
import com.dronecontrol.intelcontrol.ui.FxWindow;
import com.dronecontrol.intelcontrol.ui.SwingWindow;
import com.dronecontrol.intelcontrol.ui.data.UIAction;
import com.dronecontrol.intelcontrol.ui.listeners.UIActionListener;
import com.dronecontrol.perceptual.PerceptualController;
import com.dronecontrol.perceptual.data.DetectionType;
import com.dronecontrol.perceptual.helpers.CoordinateCalculator;
import com.google.inject.Inject;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Main implements ErrorListener, UIActionListener
{
  private final Logger logger = Logger.getLogger(ErrorListener.class);

  private final FxWindow fxWindow;

  private final SwingWindow swingWindow;

  private final DroneController droneController;

  private final DroneInputController droneInputController;

  private final PerceptualController perceptualController;

  private FxController fxController;

  private CoordinateCalculator coordinateCalculator;

  @Inject
  public Main(FxWindow fxWindow, SwingWindow swingWindow, DroneController droneController,
              DroneInputController droneInputController, PerceptualController perceptualController,
              CoordinateCalculator coordinateCalculator)
  {
    this.fxWindow = fxWindow;
    this.swingWindow = swingWindow;
    this.droneController = droneController;
    this.droneInputController = droneInputController;
    this.perceptualController = perceptualController;
    this.coordinateCalculator = coordinateCalculator;
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

    swingWindow.start();
  }

  private void addEventListeners()
  {
    droneController.addErrorListener(this);
    fxController.addUIActionListener(this);

    droneController.addVideoDataListener(fxController);
    droneController.addNavDataListener(fxController);

    droneController.addNavDataListener(droneInputController);
    droneController.addReadyStateChangeListener(droneInputController);

    perceptualController.addPictureListener(swingWindow);
    perceptualController.addDetectionListener(DetectionType.HANDS, swingWindow);
    perceptualController.addGestureListener(droneInputController);
    perceptualController.addDetectionListener(DetectionType.HANDS, droneInputController);
    perceptualController.addDetectionListener(DetectionType.HANDS, fxController);

    fxController.addUIActionListener(droneInputController);

    coordinateCalculator.addCoordinateListener(fxController);
  }

  private void startComponents()
  {
    droneController.startAsync(new Config("com.dronecontrol.leap-drone", "myProfile", 2));
    perceptualController.connect();
  }

  public void stop()
  {
    droneController.stop();
    perceptualController.disconnect();

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