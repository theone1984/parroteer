package com.tngtech.internal.intelcontrol.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tngtech.internal.droneapi.DroneController;
import com.tngtech.internal.intelcontrol.control.DroneInputController;
import com.tngtech.internal.intelcontrol.entry.Main;
import com.tngtech.internal.intelcontrol.helpers.RaceTimer;
import com.tngtech.internal.intelcontrol.ui.FxWindow;


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

        bind(RaceTimer.class).in(Singleton.class);
    }
}
