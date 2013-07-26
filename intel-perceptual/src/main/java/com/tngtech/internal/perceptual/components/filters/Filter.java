package com.tngtech.internal.perceptual.components.filters;

import com.tngtech.internal.perceptual.data.body.Coordinate;

public interface Filter {
    public Coordinate getFilteredCoordinate(Coordinate coordinate);
}