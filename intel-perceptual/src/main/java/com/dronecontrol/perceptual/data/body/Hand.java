package com.dronecontrol.perceptual.data.body;

public class Hand implements BodyPart {
    private final Coordinate coordinate;
    private final Vector direction;
    private final Vector normal;

    private final boolean active;

    public Hand(Coordinate coordinate, Vector direction, Vector normal, boolean active) {
        this.coordinate = coordinate;
        this.direction = direction;
        this.normal = normal;
        this.active = active;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Vector getDirection() {
        return direction;
    }

    public Vector getNormal() {
        return normal;
    }

    public boolean isActive() {
        return active;
    }
}