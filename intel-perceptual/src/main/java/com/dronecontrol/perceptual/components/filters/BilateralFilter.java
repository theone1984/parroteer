package com.dronecontrol.perceptual.components.filters;

import com.dronecontrol.perceptual.data.body.Coordinate;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

public class BilateralFilter implements Filter {
    public static final int SIGMA_T = 5;
    public static final double SIGMA_U = 0.03;

    private static final int HISTORY_SIZE = 5 * SIGMA_T;

    private final List<Coordinate> previousCoordinates;

    public BilateralFilter() {
        previousCoordinates = Lists.newLinkedList();
    }

    @Override
    public Coordinate getFilteredCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            removeLastCoordinate();
            return calculateSmoothedCoordinate();
        }
        addCoordinateToActiveCoordinates(coordinate);
        return calculateSmoothedCoordinate();
    }

    private void removeLastCoordinate() {
        if (previousCoordinates.size() > 0) {
            previousCoordinates.remove(0);
        }
    }

    private Coordinate calculateSmoothedCoordinate() {
        if (previousCoordinates.size() == 0) {
            return null;
        }

        float x = (float) getValue(Lists.transform(previousCoordinates, new Function<Coordinate, Double>() {
            @Override
            public Double apply(Coordinate coordinate) {
                return (double) coordinate.getX();
            }
        }));
        float y = (float) getValue(Lists.transform(previousCoordinates, new Function<Coordinate, Double>() {
            @Override
            public Double apply(Coordinate coordinate) {
                return (double) coordinate.getY();
            }
        }));
        float z = (float) getValue(Lists.transform(previousCoordinates, new Function<Coordinate, Double>() {
            @Override
            public Double apply(Coordinate coordinate) {
                return (double) coordinate.getZ();
            }
        }));

        return new Coordinate(x, y, z);
    }

    private double getValue(List<Double> values) {
        double numerator = 0;
        double denominator = 0;
        int n = values.size();

        double currentValue = values.get(values.size() - 1);
        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i);

            double exponent = (n - i) * (n - i) / (2.0 * SIGMA_T * SIGMA_T) + Math.pow(currentValue - value, 2.0) / (2 * SIGMA_U * SIGMA_U);
            double weight = Math.exp(-exponent);

            numerator += weight * value;
            denominator += weight;
        }
        return numerator / denominator;
    }

    private void addCoordinateToActiveCoordinates(Coordinate coordinate) {
        previousCoordinates.add(coordinate);
        while (previousCoordinates.size() > HISTORY_SIZE) {
            previousCoordinates.remove(0);
        }
    }
}