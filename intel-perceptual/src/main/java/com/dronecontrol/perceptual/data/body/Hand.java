package com.dronecontrol.perceptual.data.body;

public class Hand implements BodyPart {

    private final Coordinate coordinate;
    private final Coordinate unsmoothedCoordinate;

    private final boolean active;

    public Hand(Coordinate coordinate, Coordinate unsmoothedCoordinate, boolean active) {
        this.coordinate = coordinate;
        this.unsmoothedCoordinate = unsmoothedCoordinate;
        this.active = active;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Coordinate getUnsmoothedCoordinate() {
        return unsmoothedCoordinate;
    }

    public boolean isActive() {
        return active;
    }
}