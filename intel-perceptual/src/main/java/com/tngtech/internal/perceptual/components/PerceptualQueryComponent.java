package com.tngtech.internal.perceptual.components;

import com.tngtech.internal.perceptual.PerceptualPipeline;

public interface PerceptualQueryComponent {
    void queryFeatures(PerceptualPipeline pipeline);

    void processFeatures();
}