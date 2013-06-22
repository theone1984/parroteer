package com.tngtech.internal.perceptual.data.events;

import com.tngtech.internal.perceptual.data.body.Hand;

public class HandDetectionData extends DetectionData<Hand> {
    public HandDetectionData(Hand data) {
        super(data);
    }

    public Hand getHand() {
        return getData();
    }
}