package com.dronecontrol.kinectcontrol.control;

import com.dronecontrol.kinectcontrol.input.data.MovementData;
import com.dronecontrol.kinectcontrol.input.events.MovementDataListener;
import com.google.inject.Inject;
import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.data.NavData;
import com.dronecontrol.droneapi.data.enums.Camera;
import com.dronecontrol.droneapi.data.enums.FlightAnimation;
import com.dronecontrol.droneapi.data.enums.LedAnimation;
import com.dronecontrol.droneapi.listeners.NavDataListener;
import com.dronecontrol.droneapi.listeners.ReadyStateChangeListener;
import com.dronecontrol.kinectcontrol.helpers.RaceTimer;
import com.dronecontrol.kinectcontrol.ui.data.UIAction;
import com.dronecontrol.kinectcontrol.ui.listeners.UIActionListener;
import org.apache.log4j.Logger;


public class DroneInputController implements ReadyStateChangeListener, NavDataListener, UIActionListener, MovementDataListener
{
  private static final float HEIGHT_THRESHOLD = 0.25f;

  private final Logger logger = Logger.getLogger(DroneInputController.class);

  private final DroneController droneController;

  private final RaceTimer raceTimer;

  private boolean navDataReceived = false;

  private float currentHeight;

  private boolean flying = false;

  private boolean ready = false;

  @Inject
  public DroneInputController(DroneController droneController, RaceTimer raceTimer)
  {
    this.droneController = droneController;
    this.raceTimer = raceTimer;
  }

  @Override
  public void onAction(UIAction action)
  {
    switch (action)
    {
        case TAKE_OFF:
            takeOff();
            break;
        case LAND:
            land();
            break;
        case FLAT_TRIM:
            flatTrim();
            break;
        case EMERGENCY:
            emergency();
            break;
        case SWITCH_CAMERA:
            switchCamera();
            break;
        case PLAY_LED_ANIMATION:
            playLedAnimation();
            break;
        case PLAY_FLIGHT_ANIMATION:
            playFlightAnimation();
            break;
    }
  }

  @Override
  public void onNavData(NavData navData)
  {
    navDataReceived = true;
    currentHeight = navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f
            : navData.getAltitude();
    flying = navData.getState().isFlying();

    if (navData.getState().isEmergency())
    {
      raceTimer.stop();
    }
  }

  private void takeOff()
  {
    if (ready && !flying)
    {
      raceTimer.start();
      droneController.takeOff();
    }
  }

  private void land()
  {
    if (ready && flying)
    {
      raceTimer.stop();
      droneController.land();
    }
  }

  private void flatTrim()
  {
    if (ready)
    {
      droneController.flatTrim();
    }
  }

  private void emergency()
  {
    if (ready)
    {
      droneController.emergency();
    }
  }

  private void switchCamera()
  {
    if (ready)
    {
      droneController.switchCamera(Camera.NEXT);
    }
  }

  private void playLedAnimation()
  {
    if (ready)
    {
      droneController.playLedAnimation(LedAnimation.RED_SNAKE, 2.0f, 3);
    }
  }

  private void playFlightAnimation()
  {
    if (ready)
    {
      droneController.playFlightAnimation(FlightAnimation.FLIP_LEFT);
    }
  }

  @Override
  public void onMovementData(MovementData movementData)
  {
    if (movementData == null)
    {
      move(new MovementData(0, 0, 0, 0));
    }
    else
    {
      System.out.println(movementData);
      move(movementData);
    }
  }

  private void move(MovementData movementData)
  {
    if (ready)
    {
      droneController.move(movementData.getRoll(), movementData.getPitch(), movementData.getYaw(), movementData.getGaz());
    }
  }

  @Override
  public void onReadyStateChange(ReadyState readyState)
  {
    if (readyState == ReadyState.READY)
    {
      ready = true;
    } else if (readyState == ReadyState.NOT_READY)
    {
      ready = false;
    }
  }
}