package com.tngtech.internal.perceptual.data.body;

public class Hands implements BodyPart {
    private final Hand leftHand;

    private final Hand rightHand;

    public Hands(Hand leftHand, Hand rightHand) {
        this.leftHand = leftHand;
        this.rightHand = rightHand;
    }

    public Hand getLeftHand() {
        return leftHand;
    }

    public Hand getRightHand() {
        return rightHand;
    }
}