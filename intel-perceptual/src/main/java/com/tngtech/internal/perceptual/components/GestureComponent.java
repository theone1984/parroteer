package com.tngtech.internal.perceptual.components;

import com.google.common.collect.Sets;
import com.tngtech.internal.perceptual.PerceptualPipeline;
import com.tngtech.internal.perceptual.data.events.GestureData;
import com.tngtech.internal.perceptual.listeners.GestureListener;
import intel.pcsdk.PXCMGesture;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class GestureComponent implements PerceptualQueryComponent {
	private final Logger logger = Logger.getLogger(GestureComponent.class);
	
    private Set<GestureListener> gestureListeners = Sets.newHashSet();

    private PXCMGesture.Gesture gesture;

    private boolean activeBefore = false, activeBeforeLastTime = false;

    private GestureData.GestureType currentlyActiveGestureType;

    private final Map<Integer, GestureData.GestureType> gestureTypeMap = new LinkedHashMap<Integer, GestureData.GestureType>() {{
        put(PXCMGesture.Gesture.LABEL_ANY, GestureData.GestureType.NONE);
        put(PXCMGesture.Gesture.LABEL_POSE_THUMB_UP, GestureData.GestureType.THUMBS_UP);
        put(PXCMGesture.Gesture.LABEL_POSE_THUMB_DOWN, GestureData.GestureType.THUMBS_DOWN);
    }};

    public GestureComponent() {
        currentlyActiveGestureType = GestureData.GestureType.NONE;
    }

    @Override
    public void queryFeatures(PerceptualPipeline pipeline) {
        gesture = new PXCMGesture.Gesture();
        pipeline.QueryGesture(PXCMGesture.Gesture.LABEL_ANY, gesture);
    }

    @Override
    public void processFeatures() {
        if ((!gesture.active && (activeBefore || activeBeforeLastTime)) || !gestureTypeMap.containsKey(gesture.label)) {
            activeBeforeLastTime = activeBefore;
            activeBefore = gesture.active;
            return;
        }

        GestureData.GestureType gestureType = gestureTypeMap.get(gesture.label);

        if (currentlyActiveGestureType != gestureType) {
            currentlyActiveGestureType = gestureType;
            invokeGestureListeners(new GestureData(gestureType));
        }

        activeBeforeLastTime = activeBefore;
        activeBefore = gesture.active;
    }

    public void addGestureListener(GestureListener gestureListener) {
        if (!gestureListeners.contains(gestureListener)) {
            gestureListeners.add(gestureListener);
        }
    }

    public void removeGestureListener(GestureListener gestureListener) {
        if (gestureListeners.contains(gestureListener)) {
            gestureListeners.remove(gestureListener);
        }
    }

    private void invokeGestureListeners(GestureData data) {
        for (GestureListener listener : gestureListeners) {
            listener.onGesture(data);
        }
    }
}