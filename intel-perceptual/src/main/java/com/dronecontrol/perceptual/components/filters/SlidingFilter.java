package com.dronecontrol.perceptual.components.filters;

import com.google.inject.Inject;
import com.dronecontrol.perceptual.data.body.Coordinate;

import java.util.Date;

import static com.dronecontrol.perceptual.helpers.CoordinateHelper.add;
import static com.dronecontrol.perceptual.helpers.CoordinateHelper.divide;

public class SlidingFilter implements Filter {
    private static final long SLIDE_WINDOW_TIME = 4000;

    private KalmanFilterLinear2D activeFilter;

    private KalmanFilterLinear2D backgroundFilter;

    private long lastResetTimeStamp;

    @Inject
    public SlidingFilter(KalmanFilterLinear2D filter1, KalmanFilterLinear2D filter2) {
        activeFilter = filter1;
        backgroundFilter = filter2;

        lastResetTimeStamp = getCurrentTimeStamp();
    }

    public Coordinate getFilteredCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            return activeFilter.updateAndGetCoordinate(coordinate);
        }

        swapCoordinatesIfNecessary(coordinate);

        return getCoordinate(backgroundFilter.updateAndGetCoordinate(coordinate), activeFilter.updateAndGetCoordinate(coordinate));
    }

    private Coordinate getCoordinate(Coordinate coordinate1, Coordinate coordinate2) {
        return divide(add(coordinate1, coordinate2), 2.0f);
    }

    private void swapCoordinatesIfNecessary(Coordinate coordinate) {
        if (getTimeSinceLastReset() > SLIDE_WINDOW_TIME / 2) {
            lastResetTimeStamp = getCurrentTimeStamp();

            swapFilters();
            resetBackgroundFilter(coordinate);
        }
    }

    private long getTimeSinceLastReset() {
        return getCurrentTimeStamp() - lastResetTimeStamp;
    }

    private long getCurrentTimeStamp() {
        return new Date().getTime();
    }

    private void swapFilters() {
        KalmanFilterLinear2D tempFilter = activeFilter;
        activeFilter = backgroundFilter;
        backgroundFilter = tempFilter;
    }

    private void resetBackgroundFilter(Coordinate coordinate) {
        backgroundFilter.resetFilter(coordinate);
    }
}