package com.dronecontrol.perceptual.components.filters;

import com.google.inject.Inject;
import com.dronecontrol.perceptual.data.body.Coordinate;
import org.ejml.data.DenseMatrix64F;

public class KalmanFilterLinear2D {
    private static final double speed = 1.0;

    private static final DenseMatrix64F x = new DenseMatrix64F(new double[][]{{0}, {0}, {0}, {0}});

    private static final DenseMatrix64F P = new DenseMatrix64F(
            new double[][]{{5, 0, 0, 0}, {0, 5, 0, 0}, {0, 0, 5, 0}, {0, 0, 0, 5}});

    private static final DenseMatrix64F F = new DenseMatrix64F(
            new double[][]{{1, 0, speed, 0}, {0, 1, 0, speed}, {0, 0, 1, 0}, {0, 0, 0, 1}});

    private static final DenseMatrix64F R = new DenseMatrix64F(new double[][]{{5, 0}, {0, 5}});

    private static final DenseMatrix64F H = new DenseMatrix64F(new double[][]{{1, 0, 0, 0}, {0, 1, 0, 0}});

    private final KalmanFilter kalmanFilter;

    private float lastZ = 0;

    @Inject
    public KalmanFilterLinear2D(KalmanFilter kalmanFilter) {
        this.kalmanFilter = kalmanFilter;

        kalmanFilter.configure(F, H);
        kalmanFilter.setState(x, P);
    }

    public void resetFilter(Coordinate coordinate) {
        DenseMatrix64F x = new DenseMatrix64F(new double[][]{{coordinate.getX()}, {coordinate.getY()}, {0}, {0}});
        kalmanFilter.setState(x, P);
    }

    public Coordinate updateAndGetCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            return getCoordinateFromCurrentState();
        }

        DenseMatrix64F z = new DenseMatrix64F(new double[][]{{coordinate.x}, {coordinate.y}});
        kalmanFilter.predict();
        kalmanFilter.update(z, R);
        lastZ = coordinate.getZ();

        return getCoordinateFromCurrentState();
    }

    private Coordinate getCoordinateFromCurrentState() {
        DenseMatrix64F newState = kalmanFilter.getState();
        return new Coordinate((float) newState.get(0, 0), (float) newState.get(1, 0), lastZ);
    }
}