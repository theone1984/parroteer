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
        Coordinate coordinate = filter.getFilteredCoordinate(getUnsmoothedCoordinate(geoNode));
        return coordinate == null ? Coordinate.NO_COORDINATE : coordinate;
    }

    private Coordinate getUnsmoothedCoordinate(PXCMGesture.GeoNode handGeoNode) {
        if (handGeoNode.positionWorld != null) {
            return new Coordinate(handGeoNode.positionWorld.x, handGeoNode.positionWorld.z, handGeoNode.positionWorld.y);
        } else {
            return null;
        }
    }
}