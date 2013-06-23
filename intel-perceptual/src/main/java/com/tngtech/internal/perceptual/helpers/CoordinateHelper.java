package com.tngtech.internal.perceptual.helpers;

import com.tngtech.internal.perceptual.data.body.Coordinate;

public class CoordinateHelper {

    public static Coordinate add(Coordinate coordinate1, Coordinate coordinate2) {
        return new Coordinate(coordinate1.getX() + coordinate2.getX(),
                coordinate1.getY() + coordinate2.getY(), coordinate1.getZ() + coordinate2.getZ());
    }

    public static Coordinate divide(Coordinate coordinate, float divisor) {
        return multiply(coordinate, 1.0f / divisor);
    }

    public static Coordinate multiply(Coordinate coordinate, float factor) {
        return new Coordinate(coordinate.getX() * factor, coordinate.getY() * factor, coordinate.getZ() * factor);
    }

    public static Coordinate getIdentity() {
        return new Coordinate(0.0f, 0.0f, 0.0f);
    }

    public static boolean isIdentity(Coordinate coordinate) {
        return coordinate.getX() == 0.0f && coordinate.getY() == 0.0f && coordinate.getZ() == 0.0f;
    }
}
