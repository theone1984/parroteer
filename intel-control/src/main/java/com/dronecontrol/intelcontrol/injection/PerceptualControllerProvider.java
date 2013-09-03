package com.dronecontrol.intelcontrol.injection;

import com.google.inject.Provider;
import com.dronecontrol.perceptual.PerceptualController;

public class PerceptualControllerProvider implements Provider<PerceptualController> {
	@Override
	public PerceptualController get() {
		return PerceptualController.buildPerceptualController();
	}
}