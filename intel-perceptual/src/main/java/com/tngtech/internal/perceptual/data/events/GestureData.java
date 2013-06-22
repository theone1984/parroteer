package com.tngtech.internal.perceptual.data.events;

public class GestureData {
    public enum GestureType {THUMBS_UP, THUMBS_DOWN}

    private final GestureType gestureType;

    public GestureData(GestureType gestureType) {
        this.gestureType = gestureType;
    }

    public GestureType getGestureType() {
        return gestureType;
    }
}