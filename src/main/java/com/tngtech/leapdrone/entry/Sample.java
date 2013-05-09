package com.tngtech.leapdrone.entry; /******************************************************************************\
 * Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
 * Leap Motion proprietary and confidential. Not for distribution.              *
 * Use subject to the terms of the Leap Motion SDK Agreement available at       *
 * https://developer.leapmotion.com/sdk_agreement, or another agreement         *
 * between Leap Motion and you, your company or other organization.             *
 \******************************************************************************/

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.tngtech.leapdrone.control.LeapMotionDetectionEventHandler;

import java.io.IOException;

class SampleListener extends Listener
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

class Sample
{
  public static void main(String[] args)
  {
    // Create a sample listener and controller
    SampleListener listener = new SampleListener();
    Controller controller = new Controller();

    // Have the sample listener receive events from the controller
    controller.addListener(listener);

    // Keep this process running until Enter is pressed
    System.out.println("Press Enter to quit...");
    try
    {
      System.in.read();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
