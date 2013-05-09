package com.tngtech.leapdrone.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tngtech.leapdrone.drone.CommandSender;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.NavigationDataRetriever;
import com.tngtech.leapdrone.ui.SwingWindow;

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
    bind(DroneController.class).in(Singleton.class);
    bind(CommandSender.class).in(Singleton.class);
    bind(NavigationDataRetriever.class).in(Singleton.class);
    bind(SwingWindow.class).in(Singleton.class);
  }
}