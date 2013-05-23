package com.tngtech.leapdrone.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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


    primaryStage.setTitle("JavaFX 2 Login");

    BorderPane bp = new BorderPane();
    bp.setPadding(new Insets(10, 50, 50, 50));

    //Adding HBox
    HBox hb = new HBox();
    hb.setPadding(new Insets(20, 20, 20, 30));

    //Adding GridPane
    GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(20, 20, 20, 20));
    gridPane.setHgap(5);
    gridPane.setVgap(5);

    //Implementing Nodes for GridPane
    Label lblUserName = new Label("Username");
    final TextField txtUserName = new TextField();
    Label lblPassword = new Label("Password");
    final PasswordField pf = new PasswordField();
    Button btnLogin = new Button("Login");
    final Label lblMessage = new Label();

    ImageView imageView = getImageView();

    //Adding Nodes to GridPane layout
    gridPane.add(lblUserName, 0, 0);
    gridPane.add(txtUserName, 1, 0);
    gridPane.add(lblPassword, 0, 1);
    gridPane.add(pf, 1, 1);
    gridPane.add(imageView, 2, 0);
    gridPane.add(btnLogin, 2, 1);
    gridPane.add(lblMessage, 1, 2);


    //Reflection for gridPane
    Reflection r = new Reflection();
    r.setFraction(0.7f);
    gridPane.setEffect(r);

    //DropShadow effect 
    DropShadow dropShadow = new DropShadow();
    dropShadow.setOffsetX(5);
    dropShadow.setOffsetY(5);

    //Adding text and DropShadow effect to it
    Text text = new Text("JavaFX 2 Login");
    text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
    text.setEffect(dropShadow);

    //Adding text to HBox
    hb.getChildren().add(text);

    //Add ID's to Nodes
    bp.setId("bp");
    gridPane.setId("root");
    btnLogin.setId("btnLogin");
    text.setId("text");

    //Action for btnLogin
    btnLogin.setOnAction(new EventHandler<ActionEvent>()
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
    });

    //Add HBox and GridPane layout to BorderPane Layout
    bp.setTop(hb);
    bp.setCenter(gridPane);

    //Adding BorderPane to the scene and loading CSS
    Scene scene = new Scene(bp);
    scene.getStylesheets().add(getClass().getClassLoader().getResource("test.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.titleProperty().bind(scene.widthProperty().asString().concat(" : ").concat(scene.heightProperty().asString()));
    //primaryStage.setResizable(false);
    primaryStage.show();
  }

  // Can be used for displaying the incoming buffered image
  private ImageView getImageView()
  {
    BufferedImage bufferedImage = getBufferedImage();

    ImageView imageView = new ImageView();
    imageView.setImage(createFxImage(bufferedImage));
    imageView.setFitWidth(100);
    imageView.setPreserveRatio(true);
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