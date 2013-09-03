package com.dronecontrol.leapcontrol.input.leapmotion.listeners;

import com.dronecontrol.leapcontrol.input.leapmotion.data.GestureData;

public interface GestureListener {
	void onGesture(GestureData gestureData);
}