package com.dronecontrol.perceptual.listeners;

import com.dronecontrol.perceptual.data.events.GestureData;

public interface GestureListener {
	void onGesture(GestureData gestureData);
}