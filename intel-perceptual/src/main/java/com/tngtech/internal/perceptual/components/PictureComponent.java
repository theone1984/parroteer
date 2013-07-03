package com.tngtech.internal.perceptual.components;

import com.google.common.collect.Sets;
import com.tngtech.internal.perceptual.PerceptualPipeline;
import com.tngtech.internal.perceptual.data.events.PictureData;
import com.tngtech.internal.perceptual.listeners.PictureListener;

import java.awt.image.BufferedImage;
import java.util.Set;

public class PictureComponent implements PerceptualQueryComponent {

    private final Set<PictureListener> pictureListeners;

    private boolean pictureAvailable;
    private int[] pictureDimensions;
    private int[] pictureBuffer;

    public PictureComponent() {
        pictureListeners = Sets.newLinkedHashSet();

        pictureDimensions = new int[2];
        pictureBuffer = null;
    }

    @Override
    public void queryFeatures(PerceptualPipeline pipeline) {
        if (!isPictureNeeded()) {
            return;
        }

        if (isSizeAvailable()) {
            queryPicture(pipeline);
        } else {
            querySize(pipeline);
        }
    }

    private void querySize(PerceptualPipeline pipeline) {

        pictureAvailable = pipeline.QueryRGBSize(pictureDimensions);

        System.out.println("size");
    }

    private void queryPicture(PerceptualPipeline pipeline) {
        pictureAvailable = pipeline.QueryRGB(pictureBuffer);

        System.out.println("picture");
    }

    private boolean isPictureNeeded() {
        return pictureListeners.size() != 0;
    }

    private boolean isSizeAvailable() {
        return pictureBuffer != null;
    }

    @Override
    public void processFeatures() {
        if (!isPictureNeeded() || !pictureAvailable) {
            return;
        }

        if (!isSizeAvailable()) {
            determineSize();
        } else {
            determinePicture();
        }
    }

    private void determineSize() {
        pictureBuffer = new int[pictureDimensions[0] * pictureDimensions[1]];
    }

    private void determinePicture() {
        BufferedImage image = getBufferedImage(pictureBuffer);
        invokePictureListeners(new PictureData(image));
    }

    private BufferedImage getBufferedImage(int[] pictureBuffer) {
        BufferedImage image = new BufferedImage(pictureDimensions[0], pictureDimensions[1], BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < pictureDimensions[0]; x++) {
            for (int y = 0; y < pictureDimensions[1]; y++) {
                image.setRGB(x, y, pictureBuffer[y * pictureDimensions[0] + x]);
            }
        }
        return image;
    }

    public void addPictureListener(PictureListener pictureListener) {
        if (!pictureListeners.contains(pictureListener)) {
            pictureListeners.add(pictureListener);
        }
    }

    public void removePictureListener(PictureListener pictureListener) {
        if (pictureListeners.contains(pictureListener)) {
            pictureListeners.remove(pictureListener);
        }
    }

    private void invokePictureListeners(PictureData data) {
        for (PictureListener listener : pictureListeners) {
            listener.onImage(data);
        }
    }
}