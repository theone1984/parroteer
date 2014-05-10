package com.dronecontrol.perceptual.components.data;

import com.dronecontrol.perceptual.components.filters.BilateralFilter;
import com.dronecontrol.perceptual.components.filters.Filter;
import com.dronecontrol.perceptual.data.body.Coordinate;
import intel.pcsdk.PXCMGesture;

public enum GeoNode {
    LEFT_HAND(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT),
    LEFT_HAND_THUMB(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT | PXCMGesture.GeoNode.LABEL_FINGER_THUMB),
    LEFT_HAND_INDEX(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT | PXCMGesture.GeoNode.LABEL_FINGER_INDEX),
    LEFT_HAND_MIDDLE(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT | PXCMGesture.GeoNode.LABEL_FINGER_MIDDLE),
    LEFT_HAND_RING(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT | PXCMGesture.GeoNode.LABEL_FINGER_RING),
    LEFT_HAND_PINKY(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT | PXCMGesture.GeoNode.LABEL_FINGER_PINKY),

    RIGHT_HAND(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT),
    RIGHT_HAND_THUMB(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT | PXCMGesture.GeoNode.LABEL_FINGER_THUMB),
    RIGHT_HAND_INDEX(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT | PXCMGesture.GeoNode.LABEL_FINGER_INDEX),
    RIGHT_HAND_MIDDLE(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT | PXCMGesture.GeoNode.LABEL_FINGER_MIDDLE),
    RIGHT_HAND_RING(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT | PXCMGesture.GeoNode.LABEL_FINGER_RING),
    RIGHT_HAND_PINKY(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT | PXCMGesture.GeoNode.LABEL_FINGER_PINKY);

    private final int geoNodeIndex;
    private PXCMGesture.GeoNode geoNode;

    private Filter filter;

    private boolean recalculate = false;
    private Coordinate currentCoordinate;

    GeoNode(int geoNodeIndex) {
        this.geoNodeIndex = geoNodeIndex;

        geoNode = new PXCMGesture.GeoNode();
        filter = new BilateralFilter();
    }

    public int getGeoNodeIndex() {
        return geoNodeIndex;
    }

    public PXCMGesture.GeoNode getGeoNode() {
        recalculate = true;
        geoNode = new PXCMGesture.GeoNode();
        return geoNode;
    }

    public boolean isActive() {
        return geoNode.positionWorld != null;
    }

    public Coordinate getCoordinate() {
        if (recalculate) {
            recalculate = false;
            currentCoordinate = getUnsmoothedCoordinate(geoNode);
        }

        return currentCoordinate == null ? filter.getFilteredCoordinate(Coordinate.NO_COORDINATE) : currentCoordinate;
    }

    private Coordinate getUnsmoothedCoordinate(PXCMGesture.GeoNode geoNode) {
        if (geoNode.positionWorld != null) {
            return new Coordinate(geoNode.positionWorld.x, geoNode.positionWorld.z, geoNode.positionWorld.y);
        } else {
            return null;
        }
    }
}