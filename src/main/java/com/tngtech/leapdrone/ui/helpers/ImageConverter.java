package com.tngtech.leapdrone.ui.helpers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageConverter
{
  public static javafx.scene.image.Image createFxImage(BufferedImage image)
  {
    try
    {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(image, "png", out);
      out.flush();
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
      return new javafx.scene.image.Image(in);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }
}
