package com.dronecontrol.perceptual.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dronecontrol.perceptual.CamProcessor;
import com.dronecontrol.perceptual.PerceptualController;
import com.dronecontrol.perceptual.PerceptualPipeline;
import com.dronecontrol.perceptual.components.DetectionComponent;
import com.dronecontrol.perceptual.components.GestureComponent;
import com.dronecontrol.perceptual.components.PictureComponent;

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
        bind(CamProcessor.class).in(Singleton.class);
        bind(PerceptualController.class).in(Singleton.class);
        bind(PerceptualPipeline.class).in(Singleton.class);

        bind(PictureComponent.class).in(Singleton.class);
        bind(DetectionComponent.class).in(Singleton.class);
        bind(GestureComponent.class).in(Singleton.class);
    }
}