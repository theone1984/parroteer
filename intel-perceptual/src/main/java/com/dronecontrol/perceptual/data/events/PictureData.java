package com.dronecontrol.perceptual.data.events;

import java.awt.image.BufferedImage;

public class PictureData {
    private final BufferedImage image;

    public PictureData(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}