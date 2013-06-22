package com.tngtech.internal.perceptual.data.events;

import com.tngtech.internal.perceptual.data.body.Hand;
import com.tngtech.internal.perceptual.data.body.Hands;

public class HandsDetectionData extends DetectionData<Hands> {
    public HandsDetectionData(Hand leftHand, Hand rightHand) {
        super(new Hands(leftHand, rightHand));
    }

    public Hand getLeftHand() {
        return getData().getLeftHand();
    }

    public Hand getRightHand() {
        return getData().getRightHand();
    }
}