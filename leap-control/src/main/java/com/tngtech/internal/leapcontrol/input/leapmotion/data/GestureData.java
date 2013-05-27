package com.tngtech.internal.leapcontrol.input.leapmotion.data;

import com.leapmotion.leap.Gesture;

public class GestureData
{
  private final int eventId;

  private final Gesture.Type gestureType;

  public GestureData(int id, Gesture.Type gestureType)
  {
    this.eventId = id;
    this.gestureType = gestureType;
  }

  public int getId()
  {
    return eventId;
  }

  public Gesture.Type getGestureType()
  {
    return gestureType;
  }
}