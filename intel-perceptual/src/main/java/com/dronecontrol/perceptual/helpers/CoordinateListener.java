package com.dronecontrol.perceptual.helpers;

public interface CoordinateListener {
	public void onCoordinate(float roll, float pitch, float yaw, float heightDelta);
}
