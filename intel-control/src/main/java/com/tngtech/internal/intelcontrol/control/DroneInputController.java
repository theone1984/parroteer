package com.tngtech.internal.intelcontrol.control;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tngtech.internal.droneapi.DroneController;
import com.tngtech.internal.droneapi.data.NavData;
import com.tngtech.internal.droneapi.data.enums.Camera;
import com.tngtech.internal.droneapi.data.enums.FlightAnimation;
import com.tngtech.internal.droneapi.data.enums.LedAnimation;
import com.tngtech.internal.droneapi.listeners.NavDataListener;
import com.tngtech.internal.droneapi.listeners.ReadyStateChangeListener;
import com.tngtech.internal.intelcontrol.helpers.Coordinate3D;
import com.tngtech.internal.intelcontrol.helpers.RaceTimer;
import com.tngtech.internal.intelcontrol.ui.data.UIAction;
import com.tngtech.internal.intelcontrol.ui.listeners.UIActionListener;
import com.tngtech.internal.perceptual.data.body.Hand;
import com.tngtech.internal.perceptual.data.body.Hands;
import com.tngtech.internal.perceptual.data.events.DetectionData;
import com.tngtech.internal.perceptual.data.events.GestureData;
import com.tngtech.internal.perceptual.data.events.HandsDetectionData;
import com.tngtech.internal.perceptual.data.events.GestureData.GestureType;
import com.tngtech.internal.perceptual.debug.DebugHelper;
import com.tngtech.internal.perceptual.listeners.DetectionListener;
import com.tngtech.internal.perceptual.listeners.GestureListener;

import org.apache.log4j.Logger;

public class DroneInputController implements ReadyStateChangeListener, NavDataListener, UIActionListener,
		DetectionListener<Hands>, GestureListener {
	private static final float PITCH_DECAY = 0.5f;

	private static final float ROLL_DECAY = 0.5f;

	// Max height in meters
	private static final float MAX_HEIGHT = 2.0f;

	private static final float HEIGHT_THRESHOLD = 0.25f;

	private final Logger logger = Logger.getLogger(DroneInputController.class);

	private final DroneController droneController;

	private final RaceTimer raceTimer;

	private boolean navDataReceived = false;

	private float currentHeight;

	private boolean flying = false;

	private boolean expertMode = false;

	private boolean ready = false;

	private Coordinate3D leftHandReferenceCoordinates;

	private Coordinate3D rightHandReferenceCoordinates;

	@Inject
	public DroneInputController(DroneController droneController, RaceTimer raceTimer) {
		this.droneController = droneController;
		this.raceTimer = raceTimer;
	}

	@Override
	public void onAction(UIAction action) {
		switch (action) {
		case TAKE_OFF:
			takeOff();
			break;
		case LAND:
			land();
			break;
		case FLAT_TRIM:
			flatTrim();
			break;
		case EMERGENCY:
			emergency();
			break;
		case SWITCH_CAMERA:
			switchCamera();
			break;
		case PLAY_LED_ANIMATION:
			playLedAnimation();
			break;
		case PLAY_FLIGHT_ANIMATION:
			playFlightAnimation();
			break;
		case ENABLE_EXPERT_MODE:
			logger.warn("Enabling expert mode");
			expertMode = true;
			break;
		case DISABLE_EXPERT_MODE:
			logger.info("Disabling expert mode");
			expertMode = false;
			break;
		}
	}

	private float calculateHeightDelta(float desiredHeight) {
		return 3 * (desiredHeight - currentHeight) / MAX_HEIGHT;
	}

	@Override
	public void onNavData(NavData navData) {
		navDataReceived = true;
		currentHeight = navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f : navData.getAltitude();
		flying = navData.getState().isFlying();

		if (navData.getState().isEmergency()) {
			raceTimer.stop();
		}
	}

	private void takeOff() {
		if (ready && !flying) {
			raceTimer.start();
			droneController.takeOff();
		}
	}

	private void land() {
		if (ready && flying) {
			raceTimer.stop();
			droneController.land();
		}
	}

	private void flatTrim() {
		if (ready) {
			droneController.flatTrim();
		}
	}

	private void emergency() {
		if (ready) {
			droneController.emergency();
		}
	}

	private void switchCamera() {
		if (ready) {
			droneController.switchCamera(Camera.NEXT);
		}
	}

	private void playLedAnimation() {
		if (ready) {
			droneController.playLedAnimation(LedAnimation.RED_SNAKE, 2.0f, 3);
		}
	}

	private void playFlightAnimation() {
		if (ready) {
			droneController.playFlightAnimation(FlightAnimation.FLIP_LEFT);
		}
	}

	private void move(float roll, float pitch, float yaw, float heightDelta) {
		if (ready) {
			droneController.move(roll, pitch, yaw, heightDelta);
		}
	}

	@Override
	public void onReadyStateChange(ReadyState readyState) {
		if (readyState == ReadyState.READY) {
			ready = true;
		} else if (readyState == ReadyState.NOT_READY) {
			ready = false;
		}
	}

	@Override
	public void onGesture(GestureData gestureData) {
		switch (gestureData.getGestureType()) {
		case THUMBS_UP:
			logger.info("Thumbs up detected.");
			takeOff();
			break;
		case THUMBS_DOWN:
			logger.info("Thumbs down detected.");
			land();
			break;
		case BIG_FIVE:
			logger.info("Big Five detected.");
			emergency();
			flatTrim();
			break;
		}
	}

	@Override
	public void onDetection(DetectionData<Hands> handsDetectionData) {
		HandsDetectionData data = (HandsDetectionData) handsDetectionData;

		Hand leftHand = data.getLeftHand();
		Hand rightHand = data.getRightHand();

		if (leftHand.isActive() && rightHand.isActive()) {
			// As long as the drone is not in the air save last coordinates as
			// reference
			// This will stop, when the THUMBS_UP gesture is recognized
			if (ready && !flying) {
				rightHandReferenceCoordinates = new Coordinate3D(rightHand.getX(), rightHand.getY(), rightHand.getZ());
				leftHandReferenceCoordinates = new Coordinate3D(leftHand.getX(), leftHand.getY(), leftHand.getZ());
			}

			if (ready && flying) {
				float roll = getRoll(leftHand, rightHand);
				float pitch = getPitch(leftHand, rightHand);
				float yaw = getYaw(leftHand, rightHand);
				float height = getHeight(leftHand, rightHand);

				if (Math.abs(yaw) > 0.2) {
					move(roll, 0, yaw, height);
				} else {
					move(roll, pitch, yaw, height);
				}

				logger.debug(String
						.format("Roll: [%s], Pitch: [%s], Yaw: [%s], Height: [%s]", roll, pitch, yaw, height));
			}
		}
	}

	private float getHeight(Hand leftHand, Hand rightHand) {
		float height = (leftHand.getY() + rightHand.getY()) / 2;
		height = height / 0.15f;
		return height;
	}

	private float getYaw(Hand leftHand, Hand rightHand) {
		float yaw = rightHand.getZ() - leftHand.getZ();
		yaw = yaw * Math.abs(yaw);
		yaw = yaw / 0.03f;
		if (Math.abs(yaw) <= 0.2f) {
			yaw = 0;
		}
		return yaw;
	}

	private float getPitch(Hand leftHand, Hand rightHand) {
		float pitch = ((leftHand.getZ() - leftHandReferenceCoordinates.getZ()) + (rightHand.getZ() - rightHandReferenceCoordinates.z)) / 2;
		// pitch = pitch * Math.abs(pitch);
		pitch = pitch / 0.4f;
		if (Math.abs(pitch) <= 0.2f) {
			pitch = 0;
		}
		return pitch;
	}

	private float getRoll(Hand leftHand, Hand rightHand) {
		float roll = leftHand.getY() - rightHand.getY();
		roll = roll * Math.abs(roll);
		roll = roll / 0.015f;
		if (Math.abs(roll) <= 0.2f) {
			roll = 0;
		}
		return roll;
	}
}