package com.dronecontrol.perceptual.data.body;

public class Hand implements BodyPart {

    private final Coordinate coordinate;

    private final boolean active;

    public Hand(Coordinate coordinate, boolean active) {
        this.coordinate = coordinate;
        this.active = active;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public boolean isActive() {
        return active;
    }
}