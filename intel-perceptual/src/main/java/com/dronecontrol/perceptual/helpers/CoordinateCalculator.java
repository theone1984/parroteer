package com.dronecontrol.perceptual.helpers;

import com.dronecontrol.perceptual.data.body.Coordinate;
import com.dronecontrol.perceptual.data.body.Hand;
import com.dronecontrol.perceptual.data.body.Vector;
import com.google.common.collect.Lists;

import java.util.Collection;

public class CoordinateCalculator {
    private static final float MAX_PITCH = 40;
    public static final float MAX_YAW = 40;
    public static final float MAX_ROLL = 40;

    private static final float MIN_HEIGHT = 0.25f;
    private static final float MAX_HEIGHT = 1.5f;
    public static final float MIN_VALUE = 0.2f;

    private float yaw;

    private float roll;

    private float pitch;

    private float currentHeight;

    private float heightDelta;

    private Collection<CoordinateListener> coordinateListeners = Lists.newArrayList();

    public void setCurrentHeight(float currentHeight) {
        this.currentHeight = currentHeight;
    }

    public void calculateMoves(Hand hand) {
        calculateMove(hand);
        invokeCoordinateListeners();
    }

    private void invokeCoordinateListeners() {
        for (CoordinateListener coordinateListener : coordinateListeners) {
            coordinateListener.onCoordinate(roll, pitch, yaw, heightDelta);
        }
    }

    private void calculateMove(Hand hand) {
        Coordinate coordinate = hand.getCoordinate();
        Vector direction = hand.getDirection();
        Vector normal = hand.getNormal();

        roll = trim(getDegrees(getRoll(normal)) / MAX_ROLL);
        pitch = trim(getDegrees(getPitch(direction)) / MAX_PITCH);
        yaw = trim(getDegrees(getYaw(direction)) / MAX_YAW);
        heightDelta = trim(getDesiredHeight(coordinate) - currentHeight);
    }

    public void resetMove() {
        resetCoordinates();
        invokeCoordinateListeners();
    }

    private void resetCoordinates() {
        roll = 0;
        pitch = 0;
        yaw = 0;
        heightDelta = 0;
    }

    private float getDesiredHeight(Coordinate coordinate) {
        return Math.min(MAX_HEIGHT, Math.max(MIN_HEIGHT, coordinate.getZ() * 2.5f));
    }

    private float getRoll(Vector normal) {
        return -(float) Math.atan(normal.getX() / normal.getZ());
    }

    private float getPitch(Vector direction) {
        return -(float) (direction.getZ() / Math.atan(Math.sqrt(direction.getX() * direction.getX() + direction.getY() * direction.getY())));
    }

    private float getYaw(Vector direction) {
        return (float) Math.atan(direction.getX() / -direction.getY());
    }

    public float getDegrees(float radians) {
        return (float) (180 * radians / Math.PI);
    }

    private float trim(float value) {
        if (Math.abs(value) < MIN_VALUE) {
            return 0;
        }
        value = Math.signum(value) * (1 + MIN_VALUE) * (Math.abs(value) - MIN_VALUE);

        if (Math.abs(value) > 1) {
            return Math.signum(value) * 1;
        } else {
            return value;
        }
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getRoll() {
        return this.roll;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getHeightDelta() {
        return this.heightDelta;
    }

    public void addCoordinateListener(CoordinateListener coordinateListener) {
        coordinateListeners.add(coordinateListener);
    }
}