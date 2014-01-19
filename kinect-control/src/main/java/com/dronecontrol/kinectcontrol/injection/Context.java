package com.dronecontrol.kinectcontrol.injection;

import com.dronecontrol.kinectcontrol.input.KinectController;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.kinectcontrol.control.DroneInputController;
import com.dronecontrol.kinectcontrol.entry.Main;
import com.dronecontrol.kinectcontrol.helpers.RaceTimer;
import com.dronecontrol.kinectcontrol.ui.FxWindow;

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
    bind(Main.class).in(Singleton.class);
    bind(DroneInputController.class).in(Singleton.class);
    bind(FxWindow.class).in(Singleton.class);

    bind(DroneController.class).toProvider(DroneControllerProvider.class);

    bind(KinectController.class).in(Singleton.class);
    bind(RaceTimer.class).in(Singleton.class);
  }
}
