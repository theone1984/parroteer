package com.tngtech.internal.perceptual.components;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tngtech.internal.perceptual.PerceptualPipeline;
import com.tngtech.internal.perceptual.data.DetectionType;
import com.tngtech.internal.perceptual.data.body.BodyPart;
import com.tngtech.internal.perceptual.data.body.Hand;
import com.tngtech.internal.perceptual.data.events.DetectionData;
import com.tngtech.internal.perceptual.data.events.HandsDetectionData;
import com.tngtech.internal.perceptual.listeners.DetectionListener;
import intel.pcsdk.PXCMGesture;

import java.util.Map;
import java.util.Set;

public class DetectionComponent implements PerceptualQueryComponent {

    private Map<DetectionType<?>, Set<DetectionListener<?>>> detectionListeners;
    private PXCMGesture.GeoNode leftHandGeoNode;
    private PXCMGesture.GeoNode rightHandGeoNode;

    public DetectionComponent() {
        detectionListeners = Maps.newHashMap();
    }

    @Override
    public void queryFeatures(PerceptualPipeline pipeline) {
        leftHandGeoNode = new PXCMGesture.GeoNode();
        rightHandGeoNode = new PXCMGesture.GeoNode();

        pipeline.QueryGeoNode(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT, leftHandGeoNode);
        pipeline.QueryGeoNode(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT, rightHandGeoNode);
    }

    @Override
    public void processFeatures() {
        Hand rightHand = new Hand(rightHandGeoNode);
        Hand leftHand = new Hand(leftHandGeoNode);

        invokeDetectionListeners(DetectionType.HANDS, new HandsDetectionData(leftHand, rightHand));
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

    public <T extends BodyPart> void invokeDetectionListeners(DetectionType<T> detectionType, DetectionData<T> data) {
        if (!detectionListeners.containsKey(detectionType)) {
            return;
        }

        //noinspection unchecked
        //Set<DetectionListener<T>> listeners = (Set<DetectionListener<T>>) detectionListeners.get(detectionType);

        //for (DetectionListener<T> detectionListener : listeners) {
        //    detectionListener.onDetection(data);
        //}
    }
}
