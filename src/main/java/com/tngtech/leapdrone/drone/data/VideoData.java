package com.tngtech.leapdrone.drone.data;

public class VideoData
{
  private final int width;

  private final int height;

  private final int[] pixelData;

  public VideoData(int width, int height, int[] pixelData)
  {

    this.width = width;
    this.height = height;
    this.pixelData = pixelData;
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }

  public int[] getPixelData()
  {
    return pixelData;
  }
}