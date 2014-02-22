package com.dronecontrol.socketcontrol.ui;

import com.google.inject.Inject;
import com.dronecontrol.socketcontrol.helpers.RaceTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class FxWindow
{
  private RaceTimer raceTimer;

  @Inject
  public FxWindow(RaceTimer raceTimer)
  {
    this.raceTimer = raceTimer;
  }

  public FxController start(Stage primaryStage)
  {
    FXMLLoader loader = getLoader();
    Scene scene = getScene(loader);
    addStyleSheetToScene(scene);

    showStage(primaryStage, scene);

    FxController controller = loader.getController();
    controller.setRaceTimer(raceTimer);
    attachCloseEventHandler(primaryStage, controller);

    addTimer(controller);

    return controller;
  }

  private FXMLLoader getLoader()
  {
    FXMLLoader fxmlLoader = new FXMLLoader();

    fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
    return fxmlLoader;
  }

  private Scene getScene(FXMLLoader loader)
  {
    URL fxmlLocationUrl = getResourceUrl("ui/droneControl.fxml");
    loader.setLocation(fxmlLocationUrl);

    try
    {
      return new Scene((Parent) loader.load(fxmlLocationUrl.openStream()));
    } catch (IOException e)
    {
      throw new IllegalStateException("Error loading the FXML file", e);
    }
  }

  private void addStyleSheetToScene(Scene scene)
  {
    String styleSheet = getResourceUrl("ui/droneControl.css").toExternalForm();
    scene.getStylesheets().add(styleSheet);
  }

  private URL getResourceUrl(String location)
  {
    try
    {
      return Thread.currentThread().getContextClassLoader().getResource(location);
    } catch (NullPointerException e)
    {
      throw new IllegalStateException(String.format("Error loading resource %s", location));
    }
  }

  private void showStage(Stage primaryStage, Scene scene)
  {
    primaryStage.setScene(scene);

    primaryStage.setTitle("Drone control");
    primaryStage.setWidth(900);
    primaryStage.setHeight(880);

    primaryStage.show();
  }

  private void attachCloseEventHandler(Stage primaryStage, final FxController controller)
  {
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
    {
      @Override
      public void handle(WindowEvent windowEvent)
      {
        controller.onApplicationClose();
      }
    });
  }

  private void addTimer(FxController controller)
  {
    Timeline timeline = new Timeline();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(25), controller));
    timeline.playFromStart();
  }
}