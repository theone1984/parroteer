package com.dronecontrol.perceptual.components;

import com.dronecontrol.perceptual.PerceptualPipeline;
import com.dronecontrol.perceptual.components.data.GeoNode;
import com.dronecontrol.perceptual.data.DetectionType;
import com.dronecontrol.perceptual.data.body.BodyPart;
import com.dronecontrol.perceptual.data.body.Hand;
import com.dronecontrol.perceptual.data.body.Vector;
import com.dronecontrol.perceptual.data.events.DetectionData;
import com.dronecontrol.perceptual.data.events.HandsDetectionData;
import com.dronecontrol.perceptual.listeners.DetectionListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DetectionComponent implements PerceptualQueryComponent {

    private Map<DetectionType<?>, Set<DetectionListener<?>>> detectionListeners;

    private static final Collection<GeoNode> geoNodes = Lists.newArrayList(GeoNode.values());

    public DetectionComponent() {
        detectionListeners = Maps.newHashMap();
    }

    @Override
    public void queryFeatures(PerceptualPipeline pipeline) {
        for (GeoNode geoNode : geoNodes) {
            pipeline.QueryGeoNode(geoNode.getGeoNodeIndex(), geoNode.getGeoNode());
        }
    }

    @Override
    public void processFeatures() {
        Vector leftHandDirection = getLeftHandDirection();
        Vector rightHandDirection = getRightHandDirection();
        Hand rightHand = new Hand(GeoNode.LEFT_HAND.getCoordinate(), leftHandDirection, getLeftHandNormal(leftHandDirection), GeoNode.LEFT_HAND.isActive());
        Hand leftHand = new Hand(GeoNode.RIGHT_HAND.getCoordinate(), rightHandDirection, getRightHandNormal(rightHandDirection), GeoNode.RIGHT_HAND.isActive());

        //Sometimes Hands are mixed up. Switch them in case that x-position of right hand is greater than left hands x-position
        if (rightHand.isActive() && leftHand.isActive()) {
            if (rightHand.getCoordinate().getX() > leftHand.getCoordinate().getX()) {
                Hand temporaryRightHand = rightHand;
                rightHand = leftHand;
                leftHand = temporaryRightHand;
            }
        }

        invokeDetectionListeners(DetectionType.HANDS, new HandsDetectionData(leftHand, rightHand));
    }

    private Vector getLeftHandDirection() {
        return getHandDirection(GeoNode.LEFT_HAND, GeoNode.LEFT_HAND_THUMB, GeoNode.LEFT_HAND_INDEX,
                GeoNode.LEFT_HAND_MIDDLE, GeoNode.LEFT_HAND_RING, GeoNode.LEFT_HAND_PINKY);
    }

    private Vector getLeftHandNormal(Vector handDirection) {
        return getHandNormal(handDirection, true, GeoNode.LEFT_HAND, GeoNode.LEFT_HAND_THUMB,
                GeoNode.LEFT_HAND_INDEX, GeoNode.LEFT_HAND_RING, GeoNode.LEFT_HAND_PINKY);
    }

    private Vector getRightHandDirection() {
        return getHandDirection(GeoNode.RIGHT_HAND, GeoNode.RIGHT_HAND_THUMB, GeoNode.RIGHT_HAND_INDEX,
                GeoNode.RIGHT_HAND_MIDDLE, GeoNode.RIGHT_HAND_RING, GeoNode.RIGHT_HAND_PINKY);
    }

    private Vector getRightHandNormal(Vector handDirection) {
        return getHandNormal(handDirection, false, GeoNode.RIGHT_HAND, GeoNode.RIGHT_HAND_THUMB,
                GeoNode.RIGHT_HAND_INDEX, GeoNode.RIGHT_HAND_RING, GeoNode.RIGHT_HAND_PINKY);
    }

    private Vector getHandDirection(GeoNode hand, GeoNode thumb, GeoNode indexFinger,
                                    GeoNode middleFinger, GeoNode ringFinger, GeoNode pinkyFinger) {
        return plus(
                vectorBetween(thumb, hand),
                vectorBetween(indexFinger, hand),
                vectorBetween(middleFinger, hand),
                vectorBetween(ringFinger, hand),
                vectorBetween(pinkyFinger, hand)
        ).normalize();
    }

    private Vector getHandNormal(Vector direction, boolean invert, GeoNode hand, GeoNode thumb, GeoNode indexFinger,
                                 GeoNode ringFinger, GeoNode pinkyFinger) {
        Vector perpendicularVector = plus(
                vectorBetween(thumb, hand),
                vectorBetween(indexFinger, hand),
                vectorBetween(ringFinger, hand).invert(),
                vectorBetween(pinkyFinger, hand).invert()
        ).normalize().minus(direction).normalize();

        Vector normal = perpendicularVector.crossProduct(direction);
        if (invert) {
            normal = normal.invert();
        }

        return normal;
    }

    public Vector vectorBetween(GeoNode geoNode1, GeoNode geoNode2) {
        return geoNode1.getCoordinate().minus(geoNode2.getCoordinate());
    }

    public Vector plus(Vector firstVector, Vector... otherVectors) {
        Vector currentVector = firstVector;
        for (Vector vector : otherVectors) {
            currentVector = firstVector.plus(vector);
        }
        return currentVector;
    }

    public <T extends BodyPart> void addDetectionListener(DetectionType<T> detectionType, DetectionListener<T> listener) {
        if (!detectionListeners.containsKey(detectionType)) {
            detectionListeners.put(detectionType, Sets.<DetectionListener<?>>newLinkedHashSet());
        }

        Set<DetectionListener<?>> listeners = detectionListeners.get(detectionType);

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public <T extends BodyPart> void removeDetectionListener(DetectionType<T> detectionType, DetectionListener<T> listener) {
        if (!detectionListeners.containsKey(detectionType)) {
            return;
        }

        Set<DetectionListener<?>> listeners = detectionListeners.get(detectionType);

        if (!listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BodyPart> void invokeDetectionListeners(DetectionType<T> detectionType, DetectionData<T> data) {
        if (!detectionListeners.containsKey(detectionType)) {
            return;
        }

        Set<DetectionListener<?>> listeners = detectionListeners.get(detectionType);

        for (DetectionListener<?> detectionListener : listeners) {
            DetectionListener<T> specificListener = (DetectionListener<T>) detectionListener;
            specificListener.onDetection(data);
        }
    }
}