package com.dronecontrol.perceptual.data.events;

public class GestureData {
    public enum GestureType {NONE, THUMBS_UP, THUMBS_DOWN, CIRCLE, BIG_FIVE}

    private final GestureType gestureType;

    public GestureData(GestureType gestureType) {
        this.gestureType = gestureType;
    }

    public GestureType getGestureType() {
        return gestureType;
    }
}