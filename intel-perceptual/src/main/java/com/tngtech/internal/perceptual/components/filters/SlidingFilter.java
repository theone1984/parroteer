package com.tngtech.internal.perceptual.components.filters;

import com.google.inject.Inject;
import com.tngtech.internal.perceptual.data.body.Coordinate;

import java.util.Date;

import static com.tngtech.internal.perceptual.helpers.CoordinateHelper.add;
import static com.tngtech.internal.perceptual.helpers.CoordinateHelper.divide;

public class SlidingFilter {
    private static final long SLIDE_WINDOW_TIME = 250;

    private Filter activeFilter;

    private Filter backgroundFilter;

    private long lastResetTimeStamp;

    @Inject
    public SlidingFilter(Filter filter1, Filter filter2) {
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
        Filter tempFilter = activeFilter;
        activeFilter = backgroundFilter;
        backgroundFilter = tempFilter;
    }

    private void resetBackgroundFilter(Coordinate coordinate) {
        backgroundFilter.resetFilter(coordinate);
    }
}