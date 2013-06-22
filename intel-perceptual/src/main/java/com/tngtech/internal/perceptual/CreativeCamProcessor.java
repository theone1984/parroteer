package com.tngtech.internal.perceptual;

import com.google.inject.Inject;
import com.tngtech.internal.perceptual.data.body.Hand;

import intel.pcsdk.PXCMGesture;
import intel.pcsdk.PXCMGesture.Gesture;

public class CreativeCamProcessor implements Runnable {
	
	private final PerceptualPipeline perceptualPipeline;
	
	private boolean notStopped = true;
	
	@Inject
	public CreativeCamProcessor(PerceptualPipeline perceptualPipeline) {
		this.perceptualPipeline = perceptualPipeline;
	}
	
	@Override
	public void run() {
		while (notStopped) {
			queryFrame();
		}
	}
	
	public void queryFrame() {
		perceptualPipeline.AcquireFrame(false);

		Gesture gesture = new Gesture();
		PXCMGesture.GeoNode leftHandGeoNode = new PXCMGesture.GeoNode();
		PXCMGesture.GeoNode rightHandGeoNode = new PXCMGesture.GeoNode();

		perceptualPipeline.QueryGesture(PXCMGesture.Gesture.LABEL_ANY, gesture);
		perceptualPipeline.QueryGeoNode(PXCMGesture.GeoNode.LABEL_BODY_HAND_RIGHT, leftHandGeoNode);
		perceptualPipeline.QueryGeoNode(PXCMGesture.GeoNode.LABEL_BODY_HAND_LEFT, rightHandGeoNode);

		perceptualPipeline.ReleaseFrame();
		
		if (gesture.active) {
			if ( gesture.label == Gesture.LABEL_POSE_THUMB_UP ) {
				System.out.println("Thumbs Up!");
			} if ( gesture.label == Gesture.LABEL_POSE_THUMB_DOWN ) {
				System.out.println("Thumbs Down!");
			}
		}

		Hand rightHand = new Hand(rightHandGeoNode);
		Hand leftHand = new Hand(leftHandGeoNode);
		
		if (rightHand.isActive() && leftHand.isActive() ) {
			//System.out.println(String.format("Right Hand x = [%.4f], y = [%.4f], z = [%.4f]", rightHand.positionWorld.x, rightHand.positionWorld.y, rightHand.positionWorld.z));
			System.out.println(String.format("Left Hand  x = [%.4f], y = [%.4f], z = [%.4f]", leftHand.getX(), leftHand.getY(), leftHand.getZ()));
		}
	}
}
