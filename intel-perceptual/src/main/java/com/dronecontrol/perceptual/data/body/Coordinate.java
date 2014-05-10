package com.dronecontrol.perceptual.data.body;

public class Coordinate {
    public static class NoCoordinate extends Coordinate {
        public NoCoordinate() {
            super(0, 0, 0);
        }

        @Override
        public Vector minus(Coordinate coordinate) {
            return new Vector(0, 0, 0);
        }
    }

    public static final Coordinate NO_COORDINATE = new NoCoordinate();

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

    public Vector minus(Coordinate coordinate) {
        return new Vector(x - coordinate.x, y - coordinate.y, z - coordinate.z);
    }

    @Override
    public String toString() {
        return String.format("X: [%s], Y:[%s], Z:[%s]", getX(), getY(), getZ());
    }
}