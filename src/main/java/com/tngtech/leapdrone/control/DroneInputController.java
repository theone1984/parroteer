package com.tngtech.leapdrone.control;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.commands.composed.PlayFlightAnimationCommand;
import com.tngtech.leapdrone.drone.commands.composed.PlayLedAnimationCommand;
import com.tngtech.leapdrone.drone.commands.composed.SwitchCameraCommand;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.input.leapmotion.data.DetectionData;
import com.tngtech.leapdrone.input.leapmotion.data.GestureData;
import com.tngtech.leapdrone.input.leapmotion.listeners.DetectionListener;
import com.tngtech.leapdrone.input.leapmotion.listeners.GestureListener;
import com.tngtech.leapdrone.input.speech.data.SpeechData;
import com.tngtech.leapdrone.input.speech.listeners.SpeechListener;
import com.tngtech.leapdrone.ui.data.UIAction;
import com.tngtech.leapdrone.ui.listeners.UIActionListener;
import org.apache.log4j.Logger;


public class DroneInputController implements NavDataListener, DetectionListener, GestureListener, SpeechListener, UIActionListener
{
  private static final float PITCH_DECAY = 0.5f;

  private static final float ROLL_DECAY = 0.5f;

  // Max height in meters
  private static final float MAX_HEIGHT = 2.0f;

  private static final float HEIGHT_THRESHOLD = 0.25f;

  private static final float MOVE_THRESHOLD = 0.02f;

  private final Logger logger = Logger.getLogger(DroneInputController.class.getSimpleName());

  private final DroneController droneController;

  private boolean navDataReceived = false;

  private float currentHeight;

  private float lastRoll, lastPitch, lastYaw, lastHeight;

  private boolean expertMode = false;

  @Inject
  public DroneInputController(DroneController droneController)
  {
    this.droneController = droneController;
  }

  @Override
  public void onSpeech(SpeechData speechData)
  {
    String sentence = speechData.getSentence();
    if (sentence.endsWith("take off"))
    {
      droneController.takeOff();
    } else if (sentence.endsWith("land"))
    {
      droneController.land();
    } else if (sentence.endsWith("emergency"))
    {
      droneController.emergency();
    } else if (sentence.endsWith("flat trim"))
    {
      droneController.flatTrim();
    }
  }

  @Override
  public void onAction(UIAction action)
  {
    switch (action)
    {
      case TAKE_OFF:
        droneController.takeOff();
        break;
      case LAND:
        droneController.land();
        break;
      case FLAT_TRIM:
        droneController.flatTrim();
        break;
      case EMERGENCY:
        droneController.emergency();
        break;
      case SWITCH_CAMERA:
        droneController.switchCamera(SwitchCameraCommand.Camera.NEXT);
        break;
      case PLAY_LED_ANIMATION:
        droneController.playLedAnimation(PlayLedAnimationCommand.LedAnimation.RED_SNAKE, 2.0f, 3);
        break;
      case PLAY_FLIGHT_ANIMATION:
        droneController.playFlightAnimation(PlayFlightAnimationCommand.FlightAnimation.YAW_SHAKE);
        break;
      case ENABLE_EXPERT_MODE:
        logger.warn("Enabling expert mode");
        expertMode = true;
        break;
      case DISABLE_EXPERT_MODE:
        logger.info("Disabling expert mode");
        expertMode = false;
        break;
    }
  }

  @Override
  public void onDetect(DetectionData data)
  {
    float desiredHeight = data.getHeight() * MAX_HEIGHT;
    float heightDelta = navDataReceived ? calculateHeightDelta(desiredHeight)
            : 0.0f;

    if (desiredHeight <= HEIGHT_THRESHOLD)
    {
      droneController.land();
    }

    move(PITCH_DECAY * data.getRoll(), ROLL_DECAY * data.getPitch(), data.getYaw(), heightDelta);
  }

  @Override
  public void onNoDetect()
  {
    move(0.0f, 0.0f, 0.0f, 0.0f);
  }

  private void move(float roll, float pitch, float yaw, float height)
  {
    float yawToUse = expertMode ? yaw : 0.0f;

    if (Math.abs(roll - lastRoll) > MOVE_THRESHOLD
            || Math.abs(pitch - lastPitch) > MOVE_THRESHOLD
            || Math.abs(yawToUse - lastYaw) > MOVE_THRESHOLD
            || Math.abs(height - lastHeight) > MOVE_THRESHOLD)
    {
      lastRoll = roll;
      lastPitch = pitch;
      lastYaw = yawToUse;
      lastHeight = height;

      droneController.move(-roll, pitch, yaw, height);
    }
  }

  private float calculateHeightDelta(float desiredHeight)
  {
    return 3 * (desiredHeight - currentHeight) / MAX_HEIGHT;
  }

  @Override
  public void onNavData(NavData navData)
  {
    navDataReceived = true;
    currentHeight = navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f
            : navData.getAltitude();
  }

  @Override
  public void onCircle(GestureData gestureData)
  {
    droneController.takeOff();
  }
}