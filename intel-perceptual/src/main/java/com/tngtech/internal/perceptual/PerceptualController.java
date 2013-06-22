package com.tngtech.internal.perceptual;

import com.google.inject.Inject;
import com.tngtech.internal.perceptual.components.DetectionComponent;
import com.tngtech.internal.perceptual.components.GestureComponent;
import com.tngtech.internal.perceptual.data.DetectionType;
import com.tngtech.internal.perceptual.data.body.BodyPart;
import com.tngtech.internal.perceptual.injection.Context;
import com.tngtech.internal.perceptual.listeners.DetectionListener;
import com.tngtech.internal.perceptual.listeners.GestureListener;
import intel.pcsdk.PXCUPipelineJNI;
import org.apache.log4j.Logger;

public class PerceptualController {
    private final Logger logger = Logger.getLogger(PerceptualController.class);

    private final PerceptualPipeline pipeline;

    private final CamProcessor camProcessor;

    private final DetectionComponent detectionComponent;
    private final GestureComponent gestureComponent;

    public static PerceptualController buildPerceptualController() {
        return Context.getBean(PerceptualController.class);
    }

    @Inject
    public PerceptualController(PerceptualPipeline pipeline, CamProcessor listener,
                                DetectionComponent detectionComponent, GestureComponent gestureComponent) {
        this.pipeline = pipeline;
        this.camProcessor = listener;
        this.detectionComponent = detectionComponent;
        this.gestureComponent = gestureComponent;
    }

    public void connect() {
        logger.info("Connecting to intel perceptual programming creative controller");
        pipeline.Init(PXCUPipelineJNI.GESTURE);
        logger.info("Connection successfully established!");

        startCamProcessorThread();
    }

    public void startCamProcessorThread() {
        new Thread(camProcessor).start();
    }

    public void disconnect() {
        camProcessor.stop();
    }

    public void addGestureListener(GestureListener listener) {
        gestureComponent.addGestureListener(listener);
    }

    public void removeGestureListener(GestureListener listener) {
        gestureComponent.removeGestureListener(listener);
    }

    public <T extends BodyPart> void addDetectionListener(DetectionType<T> detectionType, DetectionListener<T> listener) {
        detectionComponent.addDetectionListener(detectionType, listener);
    }

    public <T extends BodyPart> void removeDetectionListener(DetectionType<T> detectionType, DetectionListener<T> listener) {
        detectionComponent.removeDetectionListener(detectionType, listener);
    }
}