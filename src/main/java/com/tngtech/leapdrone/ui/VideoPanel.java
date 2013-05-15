package com.tngtech.leapdrone.ui;

import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.data.VideoData;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VideoPanel extends javax.swing.JPanel implements VideoDataListener, NavDataListener
{
  public static final int DEFAULT_WIDTH = 320;

  public static final int DEFAULT_HEIGHT = 240;

  private AtomicReference<BufferedImage> currentImage;

  private int currentBatteryLevel = -1;

  private AtomicBoolean preserveAspect;

  private BufferedImage noConnectionImage;

  public VideoPanel()
  {
    currentImage = new AtomicReference<>();
    preserveAspect = new AtomicBoolean(true);
    noConnectionImage = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);

    initComponents();
    initializeImage();
  }

  private void initializeImage()
  {
    Graphics2D graphics = (Graphics2D) noConnectionImage.getGraphics();
    Font font = graphics.getFont().deriveFont(24.0f);
    graphics.setFont(font);
    graphics.drawString("No video connection", 40, 110);

    currentImage.set(noConnectionImage);
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  @Override
  public void onVideoData(VideoData videoData)
  {
    BufferedImage droneImage = new BufferedImage(videoData.getWidth(), videoData.getHeight(), BufferedImage.TYPE_INT_RGB);
    droneImage.setRGB(0, 0, videoData.getWidth(), videoData.getHeight(), videoData.getPixelData(), 0, videoData.getWidth());

    currentImage.set(droneImage);
    repaint();
  }

  @Override
  public void onVideoData(BufferedImage droneImage)
  {
    currentImage.set(droneImage);
    repaint();
  }

  @Override
  public void onNavData(NavData navData)
  {
    currentBatteryLevel = navData.getBatteryLevel();
  }

  @Override
  public void paintComponent(Graphics graphics)
  {
    Graphics2D graphics2D = (Graphics2D) graphics;
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    drawDroneImage(graphics2D, width, height);
    drawBatteryLevel(graphics2D);
  }

  private void drawDroneImage(Graphics2D graphics2D, int width, int height)
  {
    BufferedImage droneImage = currentImage.get();
    if (droneImage == null)
    {
      return;
    }

    graphics2D.setColor(Color.BLACK);
    graphics2D.fill3DRect(0, 0, width, height, false);

    Rectangle rectangle = getImageDimensions(width, height);
    graphics2D.drawImage(droneImage, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
  }

  private void drawBatteryLevel(Graphics2D graphics2D)
  {
    if (currentBatteryLevel == -1)
    {
      return;
    }

    String batteryLevelText = "Battery: " + currentBatteryLevel + "%";

    Font font = graphics2D.getFont().deriveFont(12.0f);
    graphics2D.setFont(font);
    graphics2D.drawString(batteryLevelText, 10, 20);
  }

  private Rectangle getImageDimensions(int width, int height)
  {
    int x = 0;
    int y = 0;

    if (preserveAspect.get())
    {
      float widthUnit = ((float) width / 4.0f);
      float heightAspect = (float) height / widthUnit;
      float heightUnit = ((float) height / 3.0f);
      float widthAspect = (float) width / heightUnit;

      if (widthAspect > 4)
      {
        x = (int) (width - (heightUnit * 4)) / 2;
        width = (int) (heightUnit * 4);
      } else if (heightAspect > 3)
      {
        y = (int) (height - (widthUnit * 3)) / 2;
        height = (int) (widthUnit * 3);
      }
    }

    return new Rectangle(x, y, width, height);
  }

  private void initComponents()
  {
    setLayout(new java.awt.GridLayout(4, 6));
  }


}