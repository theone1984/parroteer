package com.tngtech.internal.perceptual.data.body;

import intel.pcsdk.PXCMGesture.GeoNode;
import intel.pcsdk.PXCMPoint3DF32;

public class Hand implements BodyPart {

    public enum HandType {
        LEFT_HAND,
        RIGHT_HAND
    }

    private HandType handType;

    private GeoNode geoData;

    public Hand(HandType handType, GeoNode geoNode) {
        this.geoData = geoNode;
    }

    public HandType getHandType() {
        return handType;
    }

    public float getX() {
        if (isActive()) {
            return getPositionWorld().x;
        }

        return 0;
    }

    public float getY() {
        if (isActive()) {
            return getPositionWorld().z;
        }

        return 0;
    }

    public float getZ() {
        if (isActive()) {
            return getPositionWorld().y;
        }

        return 0;
    }

    public boolean isActive() {
        return this.geoData.positionWorld != null;
    }

    private PXCMPoint3DF32 getPositionWorld() {
        return geoData.positionWorld;
    }
}