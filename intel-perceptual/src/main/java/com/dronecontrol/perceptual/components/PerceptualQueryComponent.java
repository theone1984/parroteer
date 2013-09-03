package com.dronecontrol.perceptual.components;

import com.dronecontrol.perceptual.PerceptualPipeline;

public interface PerceptualQueryComponent {
    void queryFeatures(PerceptualPipeline pipeline);

    void processFeatures();
}