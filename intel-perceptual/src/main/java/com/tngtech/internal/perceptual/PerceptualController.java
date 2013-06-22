package com.tngtech.internal.perceptual;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.internal.perceptual.injection.Context;
import com.tngtech.internal.perceptual.listeners.GestureListener;
import intel.pcsdk.PXCUPipelineJNI;
import org.apache.log4j.Logger;

import java.util.Set;

public class PerceptualController {
    private final Logger logger = Logger.getLogger(PerceptualController.class);

    private final PerceptualPipeline pipeline;

    private final CreativeCamProcessor creativeCamProcessor;

    private Set<GestureListener> gestureListeners = Sets.newHashSet();

    public static PerceptualController buildPerceptualController() {
        return Context.getBean(PerceptualController.class);
    }

    @Inject
    public PerceptualController(PerceptualPipeline pipeline, CreativeCamProcessor listener) {
        this.pipeline = pipeline;
        this.creativeCamProcessor = listener;
    }

    public void connect() {
        logger.info("Connecting to intel perceptual programming creative controller");
        pipeline.Init(PXCUPipelineJNI.GESTURE);
        logger.info("Connection successfully established!");
        new Thread(creativeCamProcessor).start();
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
}