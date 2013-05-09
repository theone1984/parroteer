package com.tngtech.leapdrone.input.leapmotion;

import com.google.common.collect.Sets;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.tngtech.leapdrone.input.leapmotion.data.DetectionData;
import com.tngtech.leapdrone.input.leapmotion.listeners.DetectionListener;
import org.apache.log4j.Logger;

import java.util.Set;

public class LeapMotionListener extends Listener
{
  public static final float MAX_ROLL = 40.0f;

  public static final float MAX_PITCH = 40.0f;

  private static final float MAX_YAW = 40.0f;

  public static final float MAX_HEIGHT = 600.0f;

  private Logger logger = Logger.getLogger(LeapMotionListener.class.getSimpleName());

  private final Set<DetectionListener> detectionListeners;

  public LeapMotionListener()
  {
    detectionListeners = Sets.newHashSet();
  }

  public void onInit(Controller controller)
  {
    logger.info("Leap motion listener initialized");
  }

  public void onConnect(Controller controller)
  {
    logger.info("Leap motion listener connected");
  }

  public void onDisconnect(Controller controller)
  {
    logger.info("Leap motion listener disconnected");
  }

  public void onExit(Controller controller)
  {
    logger.info("Leap motion listener exited");
  }

  public void onFrame(Controller controller)
  {
    Frame frame = controller.frame();

    if (frame.hands().empty())
    {
      processNoDetectionEvent();
    } else
    {
      Hand hand = frame.hands().get(0);
      processDetectionEvent(hand);
    }
  }

  private void processNoDetectionEvent()
  {
    logger.trace("No hand detected");
    for (DetectionListener listener : detectionListeners)
    {
      listener.onNoDetect();
    }
  }

  public void processDetectionEvent(Hand hand)
  {
    DetectionData detectionData = getDetectionData(hand);
    logger.trace(String.format("Detected a hand - roll: %.2f, pitch: %.2f, yaw: %.2f, height: %.2f", detectionData.getRoll(),
            detectionData.getPitch(), detectionData.getYaw(), detectionData.getHeight()));

    for (DetectionListener listener : detectionListeners)
    {
      listener.onDetect(detectionData);
    }
  }

  public DetectionData getDetectionData(Hand hand)
  {
    // Get the hand's normal vector and direction
    Vector normal = hand.palmNormal();
    Vector direction = hand.direction();

    float pitch = ((Double) Math.toDegrees(direction.pitch())).floatValue() / MAX_PITCH;
    float roll = ((Double) Math.toDegrees(normal.roll())).floatValue() / MAX_ROLL;
    float yaw = ((Double) Math.toDegrees(direction.yaw())).floatValue() / MAX_YAW;
    float height = hand.palmPosition().getY() / MAX_HEIGHT;

    return new DetectionData(roll, pitch, yaw, height);
  }

  public void addDetectionListener(DetectionListener detectionListener)
  {
    if (!detectionListeners.contains(detectionListener))
    {
      detectionListeners.add(detectionListener);
    }
  }

  public void removeDetectionListener(DetectionListener detectionListener)
  {
    if (detectionListeners.contains(detectionListener))
    {
      detectionListeners.remove(detectionListener);
    }
  }
}