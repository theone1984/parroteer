package com.tngtech.leapdrone.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class FxWindow extends Application
{
  String user = "user";

  String pw = "password";

  String checkUser, checkPw;

  public static void main(String[] args)
  {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage)
  {
    primaryStage.setTitle("Drone control");

    BorderPane borderPane = new BorderPane();
    borderPane.setPadding(new Insets(10, 50, 50, 50));
    borderPane.setId("root");

    //Adding HBox
    //HBox hb = new HBox();
    //hb.setPadding(new Insets(20, 20, 20, 30));

    //Adding GridPane
    GridPane gridPane = new GridPane();
    gridPane.setHgap(5);
    gridPane.setVgap(5);

    //Implementing Nodes for GridPane
    Button buttonTakeOffLand = new Button("Take off");
    buttonTakeOffLand.setId("button-takeoff-land");
    Button buttonFlatTrim = new Button("Flat Trim");
    buttonFlatTrim.setId("button-flat-trim");
    Button buttonEmergency = new Button("Emergency");
    buttonEmergency.setId("button-emergency");
    Button buttonSwitchCamera = new Button("Switch camera");
    buttonSwitchCamera.setId("button-switch-camera");

    ImageView imageView = getImageView();

    //Adding Nodes to GridPane layout
    gridPane.add(buttonTakeOffLand, 0, 0);
    gridPane.add(buttonFlatTrim, 1, 0);
    gridPane.add(buttonEmergency, 0, 1);
    gridPane.add(buttonSwitchCamera, 1, 1);

    /*btnLogin.setOnAction(new EventHandler<ActionEvent>()
    {
      public void handle(ActionEvent event)
      {
        checkUser = txtUserName.getText();
        checkPw = pf.getText();
        if (checkUser.equals(user) && checkPw.equals(pw))
        {
          lblMessage.setText("Congratulations!");
          lblMessage.setTextFill(Color.GREEN);
        } else
        {
          lblMessage.setText("Incorrect user or pw.");
          lblMessage.setTextFill(Color.RED);
        }
        txtUserName.setText("");
        pf.setText("");
      }
    });*/

    //Add HBox and GridPane layout to BorderPane Layout
    borderPane.setTop(gridPane);
    borderPane.setCenter(imageView);

    //Adding BorderPane to the scene and loading CSS
    Scene scene = new Scene(borderPane);
    scene.getStylesheets().add(getClass().getClassLoader().getResource("test.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.titleProperty().set("Drone control");
    primaryStage.show();
  }

  // Can be used for displaying the incoming buffered image
  private ImageView getImageView()
  {
    BufferedImage bufferedImage = getBufferedImage();

    ImageView imageView = new ImageView();
    imageView.setImage(createFxImage(bufferedImage));
    imageView.setFitWidth(640);
    imageView.setFitHeight(360);
    return imageView;
  }

  private BufferedImage getBufferedImage()
  {
    try
    {
      URL url = Thread.currentThread().getContextClassLoader().getResource("flower.png");
      return ImageIO.read(url);
    } catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

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