package com.tngtech.leapdrone.ui;

import com.google.common.collect.Sets;
import com.tngtech.leapdrone.drone.data.VideoData;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.ui.data.UIAction;
import com.tngtech.leapdrone.ui.listeners.UIActionListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.Set;

public class FxController implements VideoDataListener
{
  private final Set<UIActionListener> uiActionListeners;

  @FXML
  private ImageView imageView;

  private WritableImage image;

  public FxController()
  {
    uiActionListeners = Sets.newHashSet();
  }

  public void addUIActionListener(UIActionListener uiActionlistener)
  {
    if (!uiActionListeners.contains(uiActionlistener))
    {
      uiActionListeners.add(uiActionlistener);
    }
  }

  public void removeUIActionListener(UIActionListener uiActionlistener)
  {
    if (uiActionListeners.contains(uiActionlistener))
    {
      uiActionListeners.remove(uiActionlistener);
    }
  }

  protected void onApplicationClose()
  {
    emitUIAction(UIAction.CLOSE_APPLICATION);
  }

  @FXML
  protected void onButtonTakeOffAction(ActionEvent event)
  {
    emitUIAction(UIAction.TAKE_OFF);
  }

  @FXML
  public void onButtonLandAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.LAND);
  }

  @FXML
  public void onButtonFlatTrimAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.FLAT_TRIM);
  }

  @FXML
  public void onButtonEmergencyAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.EMERGENCY);
  }

  @FXML
  public void onButtonSwitchCameraAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.SWITCH_CAMERA);
  }

  @FXML
  public void onButtonLedAnimationAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.PLAY_LED_ANIMATION);
  }

  @FXML
  public void onButtonFlightAnimationAction(ActionEvent actionEvent)
  {
    emitUIAction(UIAction.PLAY_FLIGHT_ANIMATION);
  }

  public void emitUIAction(UIAction action)
  {
    for (UIActionListener listener : uiActionListeners)
    {
      listener.onAction(action);
    }
  }

  @Override
  public void onVideoData(VideoData videoData)
  {
    BufferedImage droneImage = new BufferedImage(videoData.getWidth(), videoData.getHeight(), BufferedImage.TYPE_INT_RGB);
    droneImage.setRGB(0, 0, videoData.getWidth(), videoData.getHeight(), videoData.getPixelData(), 0, videoData.getWidth());

    onVideoData(droneImage);
  }

  @Override
  public void onVideoData(BufferedImage droneImage)
  {
    image = SwingFXUtils.toFXImage(droneImage, image);
    if (imageView.getImage() != image)
    {
      imageView.setImage(image);
    }
  }
}