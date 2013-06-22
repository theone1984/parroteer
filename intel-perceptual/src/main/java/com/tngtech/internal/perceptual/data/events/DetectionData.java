package com.tngtech.internal.perceptual.data.events;

import com.tngtech.internal.perceptual.data.body.BodyPart;

public abstract class DetectionData<T extends BodyPart> {

    private final T data;

    public DetectionData(T data) {
        this.data = data;
    }

    protected T getData() {
        return data;
    }
}