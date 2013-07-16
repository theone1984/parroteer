package com.tngtech.internal.perceptual.components.filters;

import com.google.inject.Inject;
import com.tngtech.internal.perceptual.data.body.Coordinate;
import org.ejml.data.DenseMatrix64F;

import java.util.Date;

public class Filter {
    private static final double speed = 1.0;

    private static final DenseMatrix64F x = new DenseMatrix64F(new double[][]{{0}, {0}, {0}, {0}});

    private static final DenseMatrix64F P = new DenseMatrix64F(
            new double[][]{{10, 0, 0, 0}, {0, 10, 0, 0}, {0, 0, 10, 0}, {0, 0, 0, 10}});

    private static final DenseMatrix64F F = new DenseMatrix64F(
            new double[][]{{1, 0, speed, 0}, {0, 1, 0, speed}, {0, 0, 1, 0}, {0, 0, 0, 1}});

    private static final DenseMatrix64F R = new DenseMatrix64F(new double[][]{{1, 0}, {0, 1}});

    private static final DenseMatrix64F H = new DenseMatrix64F(new double[][]{{1, 0, 0, 0}, {0, 1, 0, 0}});

    private boolean initialized = false;

    private final KalmanFilter kalmanFilter;

    @Inject
    public Filter(KalmanFilter kalmanFilter) {
        this.kalmanFilter = kalmanFilter;

        kalmanFilter.configure(F, H);
        kalmanFilter.setState(x, P);
    }

    private long lastReset = 0;

    public Coordinate getFilteredCoordinate(Coordinate coordinate, boolean found) {

        if (new Date().getTime() - lastReset > 500) {
            initialized = false;
            lastReset = new Date().getTime();
        }

        if (!initialized && found) {
            initializeCoordinate(coordinate);
            initialized = true;
        } else if (initialized && !found) {
            //initialized = false;
        }

        if (initialized && found) {
            return getFilteredValue(coordinate);
        } else {
            return coordinate;
        }
    }

    private void initializeCoordinate(Coordinate coordinate) {
        DenseMatrix64F x = new DenseMatrix64F(new double[][]{{coordinate.getX()}, {coordinate.getY()}, {0}, {0}});
        kalmanFilter.setState(x, P);
    }

    private Coordinate getFilteredValue(Coordinate coordinate) {
        DenseMatrix64F z = new DenseMatrix64F(new double[][]{{coordinate.x}, {coordinate.y}});

        kalmanFilter.predict();
        kalmanFilter.update(z, R);

        DenseMatrix64F newState = kalmanFilter.getState();

        return new Coordinate((float) newState.get(0, 0), (float) newState.get(1, 0), coordinate.getZ());
    }
}