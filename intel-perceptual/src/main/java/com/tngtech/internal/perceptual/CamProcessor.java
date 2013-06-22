package com.tngtech.internal.perceptual;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.internal.perceptual.components.PerceptualQueryComponent;
import com.tngtech.internal.perceptual.components.DetectionComponent;
import com.tngtech.internal.perceptual.components.GestureComponent;

import java.util.Collection;

public class CamProcessor implements Runnable {

    private final PerceptualPipeline pipeline;

    private final Collection<PerceptualQueryComponent> components;

    private boolean stopped;

    @Inject
    public CamProcessor(PerceptualPipeline pipeline, GestureComponent gestureComponent,
                        DetectionComponent detectionComponent) {
        this.pipeline = pipeline;

        components = Lists.newArrayList(gestureComponent, detectionComponent);
        stopped = false;
    }

    @Override
    public void run() {
        while (!stopped) {
            queryFrame();
        }
    }

    public void stop() {
        stopped = true;
    }

    public void queryFrame() {
        pipeline.AcquireFrame(false);

        for (PerceptualQueryComponent component : components) {
            component.queryFeatures(pipeline);
        }

        pipeline.ReleaseFrame();

        for (PerceptualQueryComponent component : components) {
            component.processFeatures();
        }
    }
}
