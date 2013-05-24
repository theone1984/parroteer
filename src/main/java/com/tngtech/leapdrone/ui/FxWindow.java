package com.tngtech.leapdrone.ui;

import com.google.common.collect.Sets;
import com.tngtech.leapdrone.drone.data.VideoData;
import com.tngtech.leapdrone.drone.listeners.VideoDataListener;
import com.tngtech.leapdrone.ui.data.UIAction;
import com.tngtech.leapdrone.ui.helpers.ImageConverter;
import com.tngtech.leapdrone.ui.listeners.UIActionListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.Set;

public class FxWindow implements VideoDataListener
{
  private final Set<UIActionListener> uiActionListeners;

  private Button buttonTakeOff;

  private Button buttonLand;

  private Button buttonFlatTrim;

  private Button buttonEmergency;

  private Button buttonSwitchCamera;

  private Button buttonPlayLedAnimation;

  private Button buttonPlayFlightAnimation;

  private ImageView imageView;

  public FxWindow()
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

  public void start(Stage primaryStage)
  {
    createWindow(primaryStage);
    addEventListeners();

    primaryStage.show();
  }

  public void createWindow(Stage primaryStage)
  {
    primaryStage.setTitle("Drone control");

    BorderPane borderPane = new BorderPane();
    borderPane.setPadding(new Insets(10, 50, 50, 50));
    borderPane.setId("root");

    //Adding GridPane
    GridPane gridPane = new GridPane();
    gridPane.setHgap(5);
    gridPane.setVgap(5);

    //Implementing Nodes for GridPane
    buttonTakeOff = new Button("Take off");
    gridPane.add(buttonTakeOff, 0, 0);
    buttonLand = new Button("Land");
    gridPane.add(buttonLand, 1, 0);
    buttonFlatTrim = new Button("Flat Trim");
    gridPane.add(buttonFlatTrim, 2, 0);
    buttonEmergency = new Button("Emergency");
    gridPane.add(buttonEmergency, 3, 0);
    buttonSwitchCamera = new Button("Switch camera");
    gridPane.add(buttonSwitchCamera, 0, 1);
    buttonPlayLedAnimation = new Button("Play LED animation");
    gridPane.add(buttonPlayLedAnimation, 1, 1);
    buttonPlayFlightAnimation = new Button("Play flight animation");
    gridPane.add(buttonPlayFlightAnimation, 2, 1);

    imageView = new ImageView();
    imageView.setFitWidth(640);
    imageView.setFitHeight(360);

    borderPane.setTop(gridPane);
    borderPane.setCenter(imageView);

    Scene scene = new Scene(borderPane);
    //noinspection ConstantConditions
    scene.getStylesheets().add(Thread.currentThread().getContextClassLoader().getResource("test.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.titleProperty().set("Drone control");
  }

  private void addEventListeners()
  {
    buttonTakeOff.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.TAKE_OFF);
      }
    });
    buttonLand.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.LAND);
      }
    });
    buttonFlatTrim.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.FLAT_TRIM);
      }
    });
    buttonEmergency.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.EMERGENCY);
      }
    });
    buttonSwitchCamera.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.SWITCH_CAMERA);
      }
    });
    buttonPlayLedAnimation.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.PLAY_LED_ANIMATION);
      }
    });
    buttonPlayFlightAnimation.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent actionEvent)
      {
        emitUIAction(UIAction.PLAY_FLIGHT_ANIMATION);
      }
    });
  }

  private void emitUIAction(UIAction action)
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
    Image image = ImageConverter.createFxImage(droneImage);
    imageView.setImage(image);
  }
}