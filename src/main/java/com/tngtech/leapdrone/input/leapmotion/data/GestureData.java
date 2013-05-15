package com.tngtech.leapdrone.input.leapmotion.data;

public class GestureData {
	private final int eventId;

	public GestureData(int id) {
		this.eventId = id;
	}

	public int getId() {
		return eventId;
	}
}