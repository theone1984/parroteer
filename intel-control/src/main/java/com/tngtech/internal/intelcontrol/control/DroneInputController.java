package com.tngtech.internal.intelcontrol.control;

import com.google.inject.Inject;
import com.tngtech.internal.droneapi.DroneController;
import com.tngtech.internal.droneapi.data.NavData;
import com.tngtech.internal.droneapi.data.enums.Camera;
import com.tngtech.internal.droneapi.data.enums.FlightAnimation;
import com.tngtech.internal.droneapi.data.enums.LedAnimation;
import com.tngtech.internal.droneapi.listeners.NavDataListener;
import com.tngtech.internal.droneapi.listeners.ReadyStateChangeListener;
import com.tngtech.internal.intelcontrol.helpers.RaceTimer;
import com.tngtech.internal.intelcontrol.ui.data.UIAction;
import com.tngtech.internal.intelcontrol.ui.listeners.UIActionListener;
import com.tngtech.internal.perceptual.data.body.Coordinate;
import com.tngtech.internal.perceptual.data.body.Hand;
import com.tngtech.internal.perceptual.data.body.Hands;
import com.tngtech.internal.perceptual.data.events.DetectionData;
import com.tngtech.internal.perceptual.data.events.GestureData;
import com.tngtech.internal.perceptual.data.events.HandsDetectionData;
import com.tngtech.internal.perceptual.listeners.DetectionListener;
import com.tngtech.internal.perceptual.listeners.GestureListener;
import org.apache.log4j.Logger;

public class DroneInputController implements ReadyStateChangeListener, NavDataListener, UIActionListener,
        DetectionListener<Hands>, GestureListener {

    private static final float MAX_HEIGHT = 2.0f;

    private static final float HEIGHT_THRESHOLD = 0.25f;

    private final Logger logger = Logger.getLogger(DroneInputController.class);

    private final DroneController droneController;

    private final RaceTimer raceTimer;

    private boolean flying = false;

    private boolean ready = false;

    private float currentHeight;

    private Coordinate leftHandReferenceCoordinates;

    private Coordinate rightHandReferenceCoordinates;

    private long lastCommandTimestamp;

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
        }
    }

    @Override
    public void onNavData(NavData navData) {
        flying = navData.getState().isFlying();
        currentHeight = navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f : navData.getAltitude();

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
            lastCommandTimestamp = System.currentTimeMillis();
            // As long as the drone is not in the air save last coordinates as
            // reference
            // This will stop, when the THUMBS_UP gesture is recognized
            if (ready && !flying) {
                rightHandReferenceCoordinates = rightHand.getCoordinate();
                leftHandReferenceCoordinates = leftHand.getCoordinate();
            }

            if (ready && flying) {
                float roll = getRoll(leftHand, rightHand);
                float pitch = getPitch(leftHand, rightHand);
                float yaw = getYaw(leftHand, rightHand);
                float desiredHeight = getDesiredHeight(leftHand, rightHand);
                float heightDelta = calculateHeightDelta(desiredHeight);

                if (Math.abs(yaw) > 0.1) {
                    move(0, 0, yaw, heightDelta);
                } else {
                    move(roll, pitch, yaw, heightDelta);
                }

                logger.debug(String
                        .format("Roll: [%s], Pitch: [%s], Yaw: [%s], Height: [%s]", roll, pitch, yaw, heightDelta));
            }
        } else if ((System.currentTimeMillis() - lastCommandTimestamp) >= 50) {
            // Failsafe - If no information about hands is available don't move
            move(0, 0, 0, 0);
        }
    }

    private float getDesiredHeight(Hand leftHand, Hand rightHand) {
        float height = (leftHand.getCoordinate().getY() + rightHand.getCoordinate().getY()) / 2;
        return (height / 0.15f + 0.3f) * MAX_HEIGHT;
    }

    private float calculateHeightDelta(float desiredHeight) {
        return 3 * (desiredHeight - currentHeight) / MAX_HEIGHT;
    }

    private float getYaw(Hand leftHand, Hand rightHand) {
        float yaw = rightHand.getCoordinate().getZ() - leftHand.getCoordinate().getZ();
        yaw = yaw * Math.abs(yaw);
        yaw = yaw / 0.03f;
        if (Math.abs(yaw) <= 0.1f) {
            yaw = 0;
        }
        return yaw;
    }

    private float getPitch(Hand leftHand, Hand rightHand) {
        float pitch = ((leftHand.getCoordinate().getZ() - leftHandReferenceCoordinates.getZ()) +
                (rightHand.getCoordinate().getZ() - rightHandReferenceCoordinates.z)) / 2;
        // pitch = pitch * Math.abs(pitch);
        pitch = pitch / 0.4f;
        if (Math.abs(pitch) <= 0.1f) {
            pitch = 0;
        }
        return pitch;
    }

    private float getRoll(Hand leftHand, Hand rightHand) {
        float roll = leftHand.getCoordinate().getY() - rightHand.getCoordinate().getY();
        roll = roll * Math.abs(roll);
        roll = roll / 0.015f;
        if (Math.abs(roll) <= 0.1) {
            roll = 0;
        }
        return roll;
    }
}