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

    private static final double MIN_YAW = 0.2;

	private static final float MAX_HEIGHT = 1.0f;

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
    
    private float securityScaleBuffer = 0.8f;
    
    private float maxRoll = 0.2f*securityScaleBuffer;
    
    private float maxPitch = 0.2f*securityScaleBuffer;
    
    private float maxYaw = 0.2f*securityScaleBuffer;

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

            if (ready && flying ) {
                float roll = getRoll(leftHand, rightHand);
                float pitch = getPitch(leftHand, rightHand);
                float yaw = getYaw(leftHand, rightHand);
                float desiredHeight = getDesiredHeight(leftHand, rightHand);
                float heightDelta = calculateHeightDelta(desiredHeight);

                //When changing the yaw stop rolling and pitching
                if (Math.abs(yaw) > MIN_YAW) {
                    move(0, 0, yaw, heightDelta);
                } else {
                    move(roll, pitch, 0, heightDelta);
                }

                logger.debug(String.format("Roll: [%2.3f], Pitch: [%2.3f], Yaw: [%2.3f], Height: [%2.3f]", roll, pitch, yaw, heightDelta));
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
        
        float tmp = yaw;
        
        if ( Math.abs(yaw) >= Math.abs(maxYaw) ) {
        	maxYaw= Math.abs(yaw);
        }
        
        //Scale pitch using maxPitch
        yaw = yaw/maxYaw;

        if (Math.abs(yaw) <= MIN_YAW) {
        	yaw = 0;
        }

        //logger.debug(String.format("Yaw-Max: [%2.3f], Yaw-In: [%2.3f], Yaw-Out: [%2.3f]", maxYaw, tmp, yaw));
        
        return yaw;
    }

    private float getPitch(Hand leftHand, Hand rightHand) {
        float pitch = ((leftHand.getCoordinate().getZ() - leftHandReferenceCoordinates.getZ()) +
                (rightHand.getCoordinate().getZ() - rightHandReferenceCoordinates.z)) / 2;

        float tmp = pitch;
        
        if ( Math.abs(pitch) >= Math.abs(maxPitch) ) {
        	maxPitch = Math.abs(pitch);
        }
        
        //Scale pitch using maxPitch
        pitch = pitch/maxPitch;
        //pitch = pitch * Math.abs(pitch);

        if (Math.abs(pitch) <= 0.2) {
        	pitch = 0;
        }
        
        //logger.debug(String.format("Pitch-Max: [%2.3f], Pitch-In: [%2.3f], Pitch-Out: [%2.3f]", maxPitch, tmp, pitch));
        
        return pitch;
    }

    private float getRoll(Hand leftHand, Hand rightHand) {
        float roll = leftHand.getCoordinate().getY() - rightHand.getCoordinate().getY();
        
        float tmp = roll;
        
        if ( Math.abs(roll) >= Math.abs(maxRoll) ) {
        	maxRoll = Math.abs(roll);
        }
        
        //Scale roll using maxRoll
        roll = roll / maxRoll;
        //roll = roll * Math.abs(roll);

        if (Math.abs(roll) <= 0.2) {
            roll = 0;
        }
        
        //logger.debug(String.format("Roll-Max: [%2.3f], Roll-In: [%2.3f], Roll-Out: [%2.3f]", maxRoll, tmp, roll));

        return roll;
    }
}