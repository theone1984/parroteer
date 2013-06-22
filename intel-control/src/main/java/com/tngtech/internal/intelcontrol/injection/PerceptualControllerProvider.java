package com.tngtech.internal.intelcontrol.injection;

import com.google.inject.Provider;
import com.tngtech.internal.perceptual.PerceptualController;

public class PerceptualControllerProvider implements Provider<PerceptualController> {
	@Override
	public PerceptualController get() {
		return PerceptualController.buildPerceptualController();
	}
}