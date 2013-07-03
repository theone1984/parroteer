package com.tngtech.internal.intelcontrol.ui;

import com.tngtech.internal.perceptual.data.events.PictureData;
import com.tngtech.internal.perceptual.listeners.PictureListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SwingWindow implements PictureListener {

    public class ImagePanel extends JPanel {
        private BufferedImage img = null;

        public ImagePanel() {
            setSize(640, 480);

            Timer time = new Timer(60, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    setSize(640, 480);
                    repaint();
                }
            });

            time.start();
        }

        public void setImage(BufferedImage image) {
            img = image;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image drawImage;
            if (img != null) {
                drawImage = img.getScaledInstance(-1, this.getHeight() - 4, BufferedImage.SCALE_DEFAULT);

                int xPos = (getWidth() / 2) - (drawImage.getWidth(null) / 2);
                g.drawImage(drawImage, xPos, 2, null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private ImagePanel panel;

    public void start() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        panel = new ImagePanel();
        frame.getContentPane().add(panel);

        frame.setSize(panel.getWidth(), panel.getHeight());
        frame.show();
    }

    @Override
    public void onImage(PictureData data) {
        panel.setImage(data.getImage());
    }
}