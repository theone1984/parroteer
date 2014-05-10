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
        Hand rightHand = new Hand(GeoNode.LEFT_HAND.getCoordinate(), getLeftHandDirection(), GeoNode.LEFT_HAND.isActive());
        Hand leftHand = new Hand(GeoNode.RIGHT_HAND.getCoordinate(), getRightHandDirection(), GeoNode.RIGHT_HAND.isActive());

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
        Vector combined = GeoNode.LEFT_HAND_THUMB.getCoordinate().minus(GeoNode.LEFT_HAND.getCoordinate()).plus(
                GeoNode.LEFT_HAND_INDEX.getCoordinate().minus(GeoNode.LEFT_HAND.getCoordinate()).plus(
                        GeoNode.LEFT_HAND_MIDDLE.getCoordinate().minus(GeoNode.LEFT_HAND.getCoordinate()).plus(
                                GeoNode.LEFT_HAND_RING.getCoordinate().minus(GeoNode.LEFT_HAND.getCoordinate()).plus(
                                        GeoNode.LEFT_HAND_PINKY.getCoordinate().minus(GeoNode.LEFT_HAND.getCoordinate()))
                        )
                )
        );
        return combined.normalize();
    }

    private Vector getRightHandDirection() {
        Vector combined = GeoNode.RIGHT_HAND_THUMB.getCoordinate().minus(GeoNode.RIGHT_HAND.getCoordinate()).plus(
                GeoNode.RIGHT_HAND_INDEX.getCoordinate().minus(GeoNode.RIGHT_HAND.getCoordinate()).plus(
                        GeoNode.RIGHT_HAND_MIDDLE.getCoordinate().minus(GeoNode.RIGHT_HAND.getCoordinate()).plus(
                                GeoNode.RIGHT_HAND_RING.getCoordinate().minus(GeoNode.RIGHT_HAND.getCoordinate()).plus(
                                        GeoNode.RIGHT_HAND_PINKY.getCoordinate().minus(GeoNode.RIGHT_HAND.getCoordinate()))
                        )
                )
        );
        return combined.normalize();
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