package com.dronecontrol.perceptual.data;

import com.dronecontrol.perceptual.data.body.BodyPart;
import com.dronecontrol.perceptual.data.body.Hands;

public class DetectionType<T extends BodyPart> {
    public static DetectionType<Hands> HANDS = new DetectionType<>(Hands.class);

    private Class<T> bodyPartClass;

    public DetectionType(Class<T> bodyPartClass) {
        this.bodyPartClass = bodyPartClass;
    }
}