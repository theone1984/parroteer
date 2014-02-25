package com.dronecontrol.socketcontrol.entry;

import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.data.Config;
import com.dronecontrol.droneapi.listeners.ErrorListener;
import com.dronecontrol.socketcontrol.control.DroneInputController;
import com.dronecontrol.socketcontrol.input.SocketController;
import com.dronecontrol.socketcontrol.ui.FxController;
import com.dronecontrol.socketcontrol.ui.FxWindow;
import com.dronecontrol.socketcontrol.ui.data.UIAction;
import com.dronecontrol.socketcontrol.ui.listeners.UIActionListener;
import com.google.inject.Inject;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Main implements ErrorListener, UIActionListener
{
  private final Logger logger = Logger.getLogger(ErrorListener.class);

  private final FxWindow fxWindow;

  private final DroneController droneController;

  private final SocketController socketController;

  private final DroneInputController droneInputController;

  private FxController fxController;

  @Inject
  public Main(FxWindow fxWindow, DroneController droneController, SocketController socketController,
              DroneInputController droneInputController)
  {
    this.fxWindow = fxWindow;
    this.droneController = droneController;
    this.socketController = socketController;
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
    droneController.addNavDataListener(socketController);

    droneController.addReadyStateChangeListener(droneInputController);
    socketController.addMovementDataListener(droneInputController);
    socketController.addMovementDataListener(fxController);
    socketController.addPilotActionListener(droneInputController);

    fxController.addUIActionListener(droneInputController);
  }

  private void startComponents()
  {
    droneController.startAsync(new Config("com.dronecontrol.leap-drone", "myProfile", 2));
    socketController.start();
  }

  public void stop()
  {
    droneController.stop();
    socketController.stop();

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