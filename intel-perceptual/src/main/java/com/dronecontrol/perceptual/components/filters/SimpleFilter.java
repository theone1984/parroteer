package com.dronecontrol.perceptual.components.filters;

import com.google.inject.Inject;
import com.dronecontrol.perceptual.data.body.Coordinate;

import java.util.Date;

public class SimpleFilter implements Filter {
    private static final long TIME_UNTIL_RESET = 100000;

    private KalmanFilterLinear2D filter;

    private long lastResetTimeStamp;

    @Inject
    public SimpleFilter(KalmanFilterLinear2D filter) {
        this.filter = filter;
    }

    public Coordinate getFilteredCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            return filter.updateAndGetCoordinate(coordinate);
        }

        resetCoordinateIfNecessary(coordinate);
        return filter.updateAndGetCoordinate(coordinate);
    }

    private void resetCoordinateIfNecessary(Coordinate coordinate) {
        if (getTimeSinceLastReset() > TIME_UNTIL_RESET) {
            lastResetTimeStamp = getCurrentTimeStamp();

            resetFilter(coordinate);
        }
    }

    private long getTimeSinceLastReset() {
        return getCurrentTimeStamp() - lastResetTimeStamp;
    }

    private long getCurrentTimeStamp() {
        return new Date().getTime();
    }

    private void resetFilter(Coordinate coordinate) {
        filter.resetFilter(coordinate);
    }
}