package com.dronecontrol.perceptual.data.body;

public class Coordinate {
	public final float x;
	
	public final float y;
	
	public final float z;
	
	public Coordinate(float x, float y, float z) {
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