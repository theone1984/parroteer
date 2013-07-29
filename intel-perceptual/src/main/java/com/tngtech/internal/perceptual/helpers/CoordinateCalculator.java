package com.tngtech.internal.perceptual.helpers;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.tngtech.internal.perceptual.data.body.Coordinate;
import com.tngtech.internal.perceptual.data.body.Hand;

public class CoordinateCalculator {
    private static final float MIN_ROLL = 0.2f;

	public static final float MIN_YAW = 0.2f;
    
    private static final float MAX_HEIGHT = 1.0f;
    
    private static final float MIN_PITCH = 0.2f;
    
    private Coordinate leftHandReferenceCoordinate;
	
	private Coordinate rightHandReferenceCoordinate;
	
	private float yaw;

	private float securityScaleBuffer = 0.8f;

	private float maxYaw = 0.2f*securityScaleBuffer;

	private float roll;

	private float maxRoll = 0.2f*securityScaleBuffer;

	private float pitch;
	
	private float maxPitch = 0.2f*securityScaleBuffer;
	
	private float currentHeight;

	private float desiredHeight;

	private float heightDelta;
	
	private Collection<CoordinateListener> coordinateListeners = Lists.newArrayList();

	public void setLeftHandReferenceCoordinate(Coordinate leftHandReferenceCoordinate ) {
		this.leftHandReferenceCoordinate = leftHandReferenceCoordinate;
	}

	public void setRightHandReferenceCoordinate(Coordinate rightHandReferenceCoordinate ) {
		this.rightHandReferenceCoordinate = rightHandReferenceCoordinate;
	}

	public void calculateMoves(Hand leftHand, Hand rightHand) {
		calculateYaw(leftHand, rightHand);
		calculateRoll(leftHand, rightHand);
		calculatePitch(leftHand, rightHand);
		calculateDesiredHeight(leftHand, rightHand);
		
		for ( CoordinateListener coordinateListener : coordinateListeners ) {
			coordinateListener.onCoordinate(roll, pitch, yaw, heightDelta);
		}
	}

	private void calculateDesiredHeight(Hand leftHand, Hand rightHand) {
		float height = (leftHand.getCoordinate().getY() + rightHand.getCoordinate().getY()) / 2;
        float desiredHeight = (height / 0.15f + 0.3f) * MAX_HEIGHT;
        
        this.heightDelta = (3 * (desiredHeight - currentHeight) / MAX_HEIGHT);
	}

	private void calculatePitch(Hand leftHand, Hand rightHand) {
        float pitch = ((leftHand.getCoordinate().getZ() - leftHandReferenceCoordinate.getZ()) +
                (rightHand.getCoordinate().getZ() - rightHandReferenceCoordinate.z)) / 2;

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
        
        pitch = pitch - Math.signum(pitch)*MIN_PITCH;
        //logger.debug(String.format("Pitch-Max: [%2.3f], Pitch-In: [%2.3f], Pitch-Out: [%2.3f]", maxPitch, tmp, pitch));
        
        this.pitch = pitch;
	}

	private void calculateRoll(Hand leftHand, Hand rightHand) {
        float roll = leftHand.getCoordinate().getY() - rightHand.getCoordinate().getY();
        
        float tmp = roll;
        
        if ( Math.abs(roll) >= Math.abs(maxRoll) ) {
        	maxRoll = Math.abs(roll);
        }
        
        //Scale roll using maxRoll
        roll = roll / maxRoll;
        //roll = roll * Math.abs(roll);

        if (Math.abs(roll) <= MIN_ROLL) {
            roll = 0;
        }
        
        roll = roll - Math.signum(roll)*MIN_ROLL;
        
        //logger.debug(String.format("Roll-Max: [%2.3f], Roll-In: [%2.3f], Roll-Out: [%2.3f]", maxRoll, tmp, roll));

        this.roll = roll;
	}

	private void calculateYaw(Hand leftHand, Hand rightHand) {
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

        yaw = yaw - Math.signum(yaw)*MIN_YAW;
        
        //logger.debug(String.format("Yaw-Max: [%2.3f], Yaw-In: [%2.3f], Yaw-Out: [%2.3f]", maxYaw, tmp, yaw));
        
        this.yaw = yaw;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getRoll() {
		return this.roll;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getHeightDelta() {
		return this.heightDelta;
	}
	
    public void addCoordinateListener(CoordinateListener coordinateListener) {
    	coordinateListeners.add(coordinateListener);
    }

	public boolean hasHandReferences() {
		return ( rightHandReferenceCoordinate != null ) && ( leftHandReferenceCoordinate != null );
	}
}