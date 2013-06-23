package com.tngtech.internal.intelcontrol.helpers;

public class Coordinate3D {
	public final float x;
	
	public final float y;
	
	public final float z;
	
	public Coordinate3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return String.format("X: [%s], Y:[%s], Z:[%s]", getX(), getY(), getZ());
	}
}