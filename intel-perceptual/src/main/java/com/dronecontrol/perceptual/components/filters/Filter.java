package com.dronecontrol.perceptual.components.filters;

import com.dronecontrol.perceptual.data.body.Coordinate;

public interface Filter {
    public Coordinate getFilteredCoordinate(Coordinate coordinate);
}