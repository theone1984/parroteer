package com.tngtech.internal.perceptual;

import com.tngtech.internal.perceptual.data.body.BodyPart;
import com.tngtech.internal.perceptual.data.body.Hand;

public class DetectionType<T extends BodyPart> {
    private static DetectionType<Hand> HANDS = new DetectionType<>(Hand.class);

    private Class<T> bodyPartClass;

    public DetectionType(Class<T> bodyPartClass) {
        this.bodyPartClass = bodyPartClass;
    }
}