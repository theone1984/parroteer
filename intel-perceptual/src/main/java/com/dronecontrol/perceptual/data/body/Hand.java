package com.dronecontrol.perceptual.data.body;

public class Hand implements BodyPart {
    private final Coordinate coordinate;
    private final Vector direction;

    private final boolean active;

    public Hand(Coordinate coordinate, Vector direction, boolean active) {
        this.coordinate = coordinate;
        this.direction = direction;
        this.active = active;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Vector getDirection() {
        return direction;
    }

    public boolean isActive() {
        return active;
    }
}