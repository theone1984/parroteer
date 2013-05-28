package com.tngtech.internal.leapcontrol.ui;

import com.google.common.collect.Sets;
import com.tngtech.internal.droneapi.data.NavData;
import com.tngtech.internal.droneapi.listeners.NavDataListener;
import com.tngtech.internal.droneapi.listeners.VideoDataListener;
import com.tngtech.internal.leapcontrol.ui.data.UIAction;
import com.tngtech.internal.leapcontrol.ui.listeners.UIActionListener;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.awt.image.BufferedImage;
import java.util.Set;

public class FxController implements VideoDataListener, NavDataListener, EventHandler<ActionEvent>
{
  private final Set<UIActionListener> uiActionListeners;

	@FXML
	private ImageView imageView;

	@FXML
	private VBox vbox;

	@FXML
	private Label labelBattery;

	@FXML
	private Label timer;

	private WritableImage image;

	public FxController() {
		uiActionListeners = Sets.newHashSet();
	}

	public void addUIActionListener(UIActionListener uiActionlistener) {
		if (!uiActionListeners.contains(uiActionlistener)) {
			uiActionListeners.add(uiActionlistener);
		}
	}

	public void removeUIActionListener(UIActionListener uiActionlistener) {
		if (uiActionListeners.contains(uiActionlistener)) {
			uiActionListeners.remove(uiActionlistener);
		}
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

  @FXML
  public void onCheckBoxExpertModeAction(ActionEvent actionEvent)
  {
    CheckBox checkBox = (CheckBox) actionEvent.getSource();

    emitUIAction(checkBox.isSelected() ? UIAction.ENABLE_EXPERT_MODE : UIAction.DISABLE_EXPERT_MODE);
  }

  public void emitUIAction(UIAction action)
  {
    for (UIActionListener listener : uiActionListeners)
    {
      listener.onAction(action);
    }
  }

  private void runOnFxThread(Runnable runnable)
  {
    Platform.runLater(runnable);
  }

  @Override
  public void onNavData(final NavData navData)
  {
    runOnFxThread(new Runnable()
    {
      @Override
      public void run()
      {
        setBatteryLabel(navData);
      }
    });
  }

  public void setBatteryLabel(NavData navData)
  {
    String batteryLevelText = "Battery: " + navData.getBatteryLevel() + "%";
    labelBattery.setText(batteryLevelText);

    if (navData.getState().isBatteryTooLow())
    {
      labelBattery.setTextFill(Paint.valueOf("red"));
    } else
    {
      labelBattery.setTextFill(Paint.valueOf("white"));
    }
  }

  @Override
  public void onVideoData(final BufferedImage droneImage)
  {
    runOnFxThread(new Runnable()
    {
      @Override
      public void run()
      {
        imageView.setFitWidth(vbox.getWidth() - 20);
        imageView.setFitHeight(vbox.getHeight() - 100);

        image = SwingFXUtils.toFXImage(droneImage, image);
        if (imageView.getImage() != image)
        {
          imageView.setImage(image);
        }
      }
    });
  }
  
  // Timer event
  @Override
  public void handle(ActionEvent actionEvent)
  {
    System.out.println("Event!");
  }
}