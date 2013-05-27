package com.tngtech.internal.leapcontrol.input.leapmotion.listeners;

import com.tngtech.internal.leapcontrol.input.leapmotion.data.GestureData;

public interface GestureListener {
	void onGesture(GestureData gestureData);
}