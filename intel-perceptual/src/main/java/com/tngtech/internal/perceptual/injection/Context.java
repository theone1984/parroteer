package com.tngtech.internal.perceptual.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tngtech.internal.perceptual.CamProcessor;
import com.tngtech.internal.perceptual.PerceptualController;
import com.tngtech.internal.perceptual.PerceptualPipeline;
import com.tngtech.internal.perceptual.components.DetectionComponent;
import com.tngtech.internal.perceptual.components.GestureComponent;
import com.tngtech.internal.perceptual.components.PictureComponent;

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