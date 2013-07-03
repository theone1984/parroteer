package com.tngtech.internal.perceptual.listeners;

import com.tngtech.internal.perceptual.data.events.PictureData;

import java.awt.image.BufferedImage;

public interface PictureListener {
    public void onImage(PictureData image);
}
