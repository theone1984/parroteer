package com.dronecontrol.perceptual.listeners;

import com.dronecontrol.perceptual.data.events.PictureData;

public interface PictureListener {
    public void onImage(PictureData image);
}
