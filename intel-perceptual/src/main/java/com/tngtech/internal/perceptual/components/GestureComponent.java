package com.tngtech.internal.perceptual.components;

import com.google.common.collect.Sets;
import com.tngtech.internal.perceptual.PerceptualPipeline;
import com.tngtech.internal.perceptual.data.events.GestureData;
import com.tngtech.internal.perceptual.listeners.GestureListener;
import intel.pcsdk.PXCMGesture;

import java.util.Set;

import org.apache.log4j.Logger;

public class GestureComponent implements PerceptualQueryComponent {
	private final Logger logger = Logger.getLogger(GestureComponent.class);
	
    private Set<GestureListener> gestureListeners = Sets.newHashSet();

    private PXCMGesture.Gesture gesture = null;

    @Override
    public void queryFeatures(PerceptualPipeline pipeline) {
        gesture = new PXCMGesture.Gesture();

        pipeline.QueryGesture(PXCMGesture.Gesture.LABEL_ANY, gesture);
    }

    @Override
    public void processFeatures() {
        if (gesture.active) {
            if (gesture.label == PXCMGesture.Gesture.LABEL_POSE_THUMB_UP) {
            	logger.info(String.format("Thump up with confidence [%s]", gesture.confidence));
            	invokeGestureListeners(new GestureData(GestureData.GestureType.THUMBS_UP));
            }
            
            if (gesture.label == PXCMGesture.Gesture.LABEL_POSE_THUMB_DOWN) {
            	logger.info(String.format("Thump down with confidence [%s]", gesture.confidence));
                invokeGestureListeners(new GestureData(GestureData.GestureType.THUMBS_DOWN));
            }
        }
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