package com.tngtech.internal.perceptual.components;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tngtech.internal.perceptual.DetectionListener;
import com.tngtech.internal.perceptual.DetectionType;
import com.tngtech.internal.perceptual.data.body.BodyPart;
import com.tngtech.internal.perceptual.data.events.DetectionData;

import java.util.Map;
import java.util.Set;

public class DetectionComponent {

    private Map<DetectionType<?>, Set<DetectionListener<?>>> detectionListeners;

    public DetectionComponent() {
        detectionListeners = Maps.newHashMap();
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
        Set<DetectionListener<T>> listeners = (Set<DetectionListener<T>>) detectionListeners.get(detectionType);

        for (DetectionListener<T> detectionListener : listeners) {
            detectionListener.onDetection(data);
        }
    }

}
