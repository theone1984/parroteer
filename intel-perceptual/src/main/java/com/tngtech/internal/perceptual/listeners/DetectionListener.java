package com.tngtech.internal.perceptual.listeners;

import com.tngtech.internal.perceptual.data.body.BodyPart;
import com.tngtech.internal.perceptual.data.events.DetectionData;

public interface DetectionListener<B extends BodyPart> {
    public void onDetection(DetectionData<B> data);
}