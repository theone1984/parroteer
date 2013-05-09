package com.tngtech.leapdrone.control.leapmotion;

import com.google.common.collect.Sets;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.tngtech.leapdrone.control.leapmotion.data.DetectionData;
import com.tngtech.leapdrone.control.leapmotion.listeners.DetectionListener;

import java.util.Set;
import java.util.logging.Logger;

public class LeapMotionListener extends Listener
{
  public final static int MAX_HEIGHT = 600;

  public final static int MAX_ROLL = 40;

  public final static int MAX_PITCH = 40;

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

    if (!frame.hands().empty())
    {
      Hand hand = frame.hands().get(0);
      processDetectionEvent(hand);
    }
  }

  public void processDetectionEvent(Hand hand)
  {
    DetectionData detectionData = getDetectionData(hand);
    logger.fine("Detected a hand");

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

    float handHeight = hand.palmPosition().getY();
    float handPitchInDegrees = ((Double) Math.toDegrees(direction.pitch())).floatValue();
    float handRollInDegrees = ((Double) Math.toDegrees(normal.roll())).floatValue();

    float roll = handRollInDegrees / MAX_ROLL;
    float pitch = handPitchInDegrees / MAX_PITCH;
    float height = handHeight / MAX_HEIGHT;

    return new DetectionData(roll, pitch, 0.0f, height);
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