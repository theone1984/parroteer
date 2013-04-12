package com.tngtech.leapdrone.control;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

class LeapMotionListener extends Listener
{
  public final static int MAX_HEIGHT = 600;

  public final static int MAX_ROLL = 40;

  public final static int MAX_PITCH = 40;

  private LeapMotionDetectionEventHandler eventHandler;

  public void onInit(Controller controller)
  {
    System.out.println("Initialized");
  }

  public void onConnect(Controller controller)
  {
    System.out.println("Connected");
  }

  public void onDisconnect(Controller controller)
  {
    System.out.println("Disconnected");
  }

  public void onExit(Controller controller)
  {
    System.out.println("Exited");
  }

  public void onFrame(Controller controller)
  {
    // Get the most recent frame and report some basic information
    Frame frame = controller.frame();

    if (!frame.hands().empty())
    {
      // Get the first hand
      Hand hand = frame.hands().get(0);

      // Get the hand's normal vector and direction
      Vector normal = hand.palmNormal();
      Vector direction = hand.direction();

      float handHeight = hand.palmPosition().getY();
      float handPitchInDegrees = ((Double) Math.toDegrees(direction.pitch())).floatValue();
      float handRollInDegrees = ((Double) Math.toDegrees(normal.roll())).floatValue();

      float gaz = handHeight / MAX_HEIGHT;
      float pitch = handPitchInDegrees / MAX_PITCH;
      float roll = handRollInDegrees / MAX_ROLL;

      if (eventHandler != null)
      {
        eventHandler.onEvent(gaz, pitch, roll);
      }

    }
  }

  public void setDetectionEventHandler(LeapMotionDetectionEventHandler eventHandler)
  {
    this.eventHandler = eventHandler;
  }
}