package com.dronecontrol.droneapi.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dronecontrol.droneapi.CommandSender;
import com.dronecontrol.droneapi.CommandSenderCoordinator;
import com.dronecontrol.droneapi.ConfigurationDataRetriever;
import com.dronecontrol.droneapi.DroneController;
import com.dronecontrol.droneapi.DroneStartupCoordinator;
import com.dronecontrol.droneapi.InternalStateWatcher;
import com.dronecontrol.droneapi.NavigationDataRetriever;
import com.dronecontrol.droneapi.VideoRetrieverH264;
import com.dronecontrol.droneapi.VideoRetrieverP264;
import com.dronecontrol.droneapi.components.ErrorListenerComponent;

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
    bind(DroneStartupCoordinator.class).in(Singleton.class);
    bind(CommandSender.class).in(Singleton.class);
    bind(CommandSenderCoordinator.class).in(Singleton.class);
    bind(InternalStateWatcher.class).in(Singleton.class);
    bind(NavigationDataRetriever.class).in(Singleton.class);
    bind(VideoRetrieverH264.class).in(Singleton.class);
    bind(VideoRetrieverP264.class).in(Singleton.class);
    bind(ConfigurationDataRetriever.class).in(Singleton.class);
    bind(ErrorListenerComponent.class).in(Singleton.class);
  }
}