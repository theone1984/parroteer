package com.dronecontrol.intelcontrol.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.intelcontrol.control.DroneInputController;
import com.dronecontrol.intelcontrol.entry.Main;
import com.dronecontrol.intelcontrol.helpers.RaceTimer;
import com.dronecontrol.intelcontrol.ui.FxWindow;
import com.dronecontrol.perceptual.PerceptualController;
import com.dronecontrol.perceptual.helpers.CoordinateCalculator;


public class Context extends AbstractModule {
    private static Injector injector;

    public static <T> T getBean(Class<T> clazz) {
        if (injector == null) {
            injector = Guice.createInjector(new Context());
        }
        return injector.getInstance(clazz);
    }

    // Used for value builder
    @SuppressWarnings("UnusedDeclaration")
    protected Injector getInjector() {
        return injector;
    }

    @Override
    protected void configure() {
        bind(Main.class).in(Singleton.class);
        bind(DroneInputController.class).in(Singleton.class);
        bind(FxWindow.class).in(Singleton.class);

        bind(DroneController.class).toProvider(DroneControllerProvider.class);
        bind(PerceptualController.class).toProvider(PerceptualControllerProvider.class);

        bind(RaceTimer.class).in(Singleton.class);
        
        bind(CoordinateCalculator.class).in(Singleton.class);
    }
}
