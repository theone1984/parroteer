package com.dronecontrol.kinectcontrol.injection;

import com.dronecontrol.kinectcontrol.config.Config;
import com.dronecontrol.kinectcontrol.input.KinectController;
import com.dronecontrol.kinectcontrol.input.KinectDataReceiver;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.kinectcontrol.control.DroneInputController;
import com.dronecontrol.kinectcontrol.entry.Main;
import com.dronecontrol.kinectcontrol.helpers.RaceTimer;
import com.dronecontrol.kinectcontrol.ui.FxWindow;
import com.tngtech.configbuilder.ConfigBuilder;

public class Context extends AbstractModule
{
  private static Injector injector;

  public static <T> T getBean(Class<T> clazz)
  {
    if (injector == null)
    {
      injector = Guice.createInjector(new Context());
    }
    return injector.getInstance(clazz);
  }

  // Used for value builder
  @SuppressWarnings("UnusedDeclaration")
  protected Injector getInjector()
  {
    return injector;
  }

  @Override
  protected void configure()
  {
    bindBeansToImplementations();
    bindBeansToScope();
    bindBeansToProviders();
  }

  private void bindBeansToImplementations()
  {
    bind(Config.class).toInstance(getConfig());
  }

  private void bindBeansToScope()
  {
    bind(Main.class).in(Singleton.class);
    bind(DroneInputController.class).in(Singleton.class);
    bind(FxWindow.class).in(Singleton.class);

    bind(KinectController.class).in(Singleton.class);
    bind(KinectDataReceiver.class).in(Singleton.class);
    bind(RaceTimer.class).in(Singleton.class);
  }

  private void bindBeansToProviders()
  {
    bind(DroneController.class).toProvider(DroneControllerProvider.class);
  }

  private Config getConfig()
  {
    return new ConfigBuilder<>(Config.class).build();
  }
}