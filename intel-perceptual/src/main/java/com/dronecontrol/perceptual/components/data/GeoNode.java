package com.dronecontrol.perceptual.components.data;

import com.dronecontrol.perceptual.components.filters.BilateralFilter;
import com.dronecontrol.perceptual.components.filters.Filter;
import com.dronecontrol.perceptual.data.body.Coordinate;
import intel.pcsdk.PXCMGesture;

public enum GeoNode {
    LEFT_HAND(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT),
    RIGHT_HAND(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT);

    private final int geoNodeIndex;
    private final PXCMGesture.GeoNode geoNode;
    private Filter filter;

    GeoNode(int geoNodeIndex) {
        this.geoNodeIndex = geoNodeIndex;

        geoNode = new PXCMGesture.GeoNode();
        filter = new BilateralFilter();
    }

    public int getGeoNodeIndex() {
        return geoNodeIndex;
    }

    public PXCMGesture.GeoNode getGeoNode() {
        return geoNode;
    }

    public boolean isActive() {
        return geoNode.positionWorld != null;
    }

    public Coordinate getCoordinate() {
        return filter.getFilteredCoordinate(getUnsmoothedCoordinate(geoNode));
    }

    private Coordinate getUnsmoothedCoordinate(PXCMGesture.GeoNode handGeoNode) {
        if (handGeoNode.positionWorld != null) {
            return new Coordinate(handGeoNode.positionWorld.x, handGeoNode.positionWorld.z, handGeoNode.positionWorld.y);
        } else {
            return null;
        }
    }
}