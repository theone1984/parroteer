package com.dronecontrol.perceptual.listeners;

import com.dronecontrol.perceptual.data.body.BodyPart;
import com.dronecontrol.perceptual.data.events.DetectionData;

public interface DetectionListener<B extends BodyPart> {
    public void onDetection(DetectionData<B> data);
}