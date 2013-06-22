package com.tngtech.internal.perceptual.listeners;

import com.tngtech.internal.perceptual.data.events.GestureData;

public interface GestureListener {
	void onGesture(GestureData gestureData);
}