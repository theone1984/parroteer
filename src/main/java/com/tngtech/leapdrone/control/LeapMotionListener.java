package com.tngtech.leapdrone.control;

import java.util.Locale;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

class LeapMotionListener extends Listener {
	private float handHeight = 0;
	
    private double handPitchInDegrees = 0;
	
    private double handRollInDegrees = 0;

	public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();

        if (!frame.hands().empty()) {
            System.out.println(String.format("Frame Id: %s, Timestamp: %s", frame.id(), frame.timestamp()));
            // Get the first hand
            Hand hand = frame.hands().get(0);

            // Check if the hand has any fingers
            FingerList fingers = hand.fingers();
            if (!fingers.empty()) {
                // Calculate the hand's average finger tip position
                Vector avgPos = Vector.zero();
                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                }
                avgPos = avgPos.divide(fingers.count());
            }

            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();

            this.handHeight = hand.palmPosition().getY();
            this.handPitchInDegrees = Math.toDegrees(direction.pitch());
            this.handRollInDegrees = Math.toDegrees(normal.roll());

            // Calculate the hand's pitch, roll, and yaw angles
            System.out.println(String.format(Locale.GERMAN, "Height: [%6.02f], Hand Pitch: [%6.02f] degrees, Hand Roll: [%6.02f] degrees", handHeight, handPitchInDegrees, handRollInDegrees));
        }

        if (!frame.hands().empty() ) {
            System.out.println();
        }
    }
    
    public float getHandHeight() {
		return handHeight;
	}

	public void setHandHeight(float handHeight) {
		this.handHeight = handHeight;
	}

	public double getHandPitchInDegrees() {
		return handPitchInDegrees;
	}

	public void setHandPitchInDegrees(double handPitchInDegrees) {
		this.handPitchInDegrees = handPitchInDegrees;
	}

	public double getHandRollInDegrees() {
		return handRollInDegrees;
	}

	public void setHandRollInDegrees(double handRollInDegrees) {
		this.handRollInDegrees = handRollInDegrees;
	}
}