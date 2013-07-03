package com.tngtech.internal.intelcontrol.ui;

import com.tngtech.internal.perceptual.data.events.PictureData;
import com.tngtech.internal.perceptual.listeners.PictureListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SwingWindow implements PictureListener {

    private static class ImagePanel extends JPanel {
        BufferedImage image;

        public void paintComponent(Graphics graphics) {
            if (image == null) {
                return;
            }

            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.drawImage(image, null, 0, 0);
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }
    }

    private ImagePanel panel;

    public void start() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        panel = new ImagePanel();
        frame.getContentPane().add(panel);

        frame.setSize(800, 600);
        frame.show();
    }

    @Override
    public void onImage(PictureData data) {
        panel.setImage(data.getImage());
    }
}