package com.dronecontrol.perceptual;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.dronecontrol.perceptual.components.DetectionComponent;
import com.dronecontrol.perceptual.components.GestureComponent;
import com.dronecontrol.perceptual.components.PerceptualQueryComponent;
import com.dronecontrol.perceptual.components.PictureComponent;

import java.util.Collection;

public class CamProcessor implements Runnable {

    private final PerceptualPipeline pipeline;

    private final Collection<PerceptualQueryComponent> components;

    private boolean stopped;

    @Inject
    public CamProcessor(PerceptualPipeline pipeline, PictureComponent pictureComponent,
                        GestureComponent gestureComponent, DetectionComponent detectionComponent) {
        this.pipeline = pipeline;

        components = Lists.newArrayList(pictureComponent, gestureComponent, detectionComponent);
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
        pipeline.AcquireFrame(true);

        for (PerceptualQueryComponent component : components) {
            component.queryFeatures(pipeline);
        }

        pipeline.ReleaseFrame();

        for (PerceptualQueryComponent component : components) {
            component.processFeatures();
        }
    }

    private void sleep(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
        }
    }
}
