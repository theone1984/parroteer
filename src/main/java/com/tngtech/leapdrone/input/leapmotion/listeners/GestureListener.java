package com.tngtech.leapdrone.input.leapmotion.listeners;

import com.tngtech.leapdrone.input.leapmotion.data.GestureData;

public interface GestureListener {
	void onCircle(GestureData gestureData);
}