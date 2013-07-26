package com.tngtech.internal.intelcontrol.ui;

import com.google.common.collect.Maps;
import com.tngtech.internal.perceptual.data.body.Coordinate;
import com.tngtech.internal.perceptual.data.body.Hands;
import com.tngtech.internal.perceptual.data.events.DetectionData;
import com.tngtech.internal.perceptual.data.events.HandsDetectionData;
import com.tngtech.internal.perceptual.data.events.PictureData;
import com.tngtech.internal.perceptual.listeners.DetectionListener;
import com.tngtech.internal.perceptual.listeners.PictureListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;

public class SwingWindow implements PictureListener, DetectionListener<Hands> {

    private static final double OPEN_FACTOR_X = 45.0;
    private static final double OPEN_FACTOR_Y = 35.0;

    public class ImagePanel extends JPanel {
        private static final int CIRCLE_DIAMETER = 20;

        private BufferedImage image = null;
        private Map<Color, Coordinate> coordinates;

        public ImagePanel() {
            coordinates = Maps.newLinkedHashMap();

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

        public void setData(BufferedImage image, Map<Color, Coordinate> coordinates) {
            this.image = image;
            this.coordinates = Maps.newLinkedHashMap(coordinates);
        }

        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            drawBackground(graphics);
            drawCoordinates(graphics);
        }

        private void drawBackground(Graphics graphics) {
            //
            // Image drawImage;
            if (image != null) {
                graphics.drawImage(image, 0, 0, null);
            } else {
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        private void drawCoordinates(Graphics graphics) {
            for (Map.Entry<Color, Coordinate> entry : coordinates.entrySet()) {
                drawCoordinate(graphics, entry.getKey(), entry.getValue());
            }
        }

        private void drawCoordinate(Graphics graphics, Color color, Coordinate coordinate) {
            graphics.setColor(color);

            int x = this.getWidth() / 2 + (int) Math.round((Math.toDegrees(Math.atan(coordinate.getX() / coordinate.getZ())) / OPEN_FACTOR_X) * this.getWidth());
            int y = this.getHeight() / 2 - (int) Math.round((Math.toDegrees(Math.atan(coordinate.getY() / coordinate.getZ())) / OPEN_FACTOR_Y) * this.getHeight());

            graphics.fillArc(x + CIRCLE_DIAMETER / 2, y + CIRCLE_DIAMETER / 2, CIRCLE_DIAMETER, CIRCLE_DIAMETER, 0, 360);
        }
    }

    private ImagePanel panel;

    private BufferedImage image;

    private Map<Color, Coordinate> coordinates;

    public SwingWindow() {
        coordinates = Maps.newLinkedHashMap();
    }

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
        if (data.getImage() != null) {
            this.image = data.getImage();
            panel.setData(data.getImage(), coordinates);
        }
    }

    @Override
    public void onDetection(DetectionData<Hands> data) {
        HandsDetectionData handsDetectionData = (HandsDetectionData) data;

        //coordinates = Maps.newLinkedHashMap();
        if (handsDetectionData.getLeftHand().isActive()) {
            coordinates.put(Color.BLUE, handsDetectionData.getLeftHand().getCoordinate());
            coordinates.put(Color.MAGENTA, handsDetectionData.getLeftHand().getUnsmoothedCoordinate());
        }
        if (handsDetectionData.getRightHand().isActive()) {
            coordinates.put(Color.RED, handsDetectionData.getRightHand().getCoordinate());
            coordinates.put(Color.CYAN, handsDetectionData.getRightHand().getUnsmoothedCoordinate());
        }

        panel.setData(image, coordinates);
    }
}