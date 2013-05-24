package com.tngtech.leapdrone.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tngtech.leapdrone.control.DroneInputController;
import com.tngtech.leapdrone.drone.CommandSender;
import com.tngtech.leapdrone.drone.CommandSenderCoordinator;
import com.tngtech.leapdrone.drone.ConfigurationDataRetriever;
import com.tngtech.leapdrone.drone.DroneController;
import com.tngtech.leapdrone.drone.DroneCoordinator;
import com.tngtech.leapdrone.drone.NavigationDataRetriever;
import com.tngtech.leapdrone.drone.VideoRetrieverH264;
import com.tngtech.leapdrone.drone.VideoRetrieverP264;
import com.tngtech.leapdrone.drone.components.ErrorListenerComponent;
import com.tngtech.leapdrone.input.leapmotion.LeapMotionController;
import com.tngtech.leapdrone.input.leapmotion.LeapMotionListener;
import com.tngtech.leapdrone.input.speech.SpeechDetector;
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
    bind(DroneCoordinator.class).in(Singleton.class);
    bind(CommandSender.class).in(Singleton.class);
    bind(CommandSenderCoordinator.class).in(Singleton.class);
    bind(NavigationDataRetriever.class).in(Singleton.class);
    bind(VideoRetrieverH264.class).in(Singleton.class);
    bind(VideoRetrieverP264.class).in(Singleton.class);
    bind(ConfigurationDataRetriever.class).in(Singleton.class);
    bind(ErrorListenerComponent.class).in(Singleton.class);

    bind(LeapMotionController.class).in(Singleton.class);
    bind(LeapMotionListener.class).in(Singleton.class);

    bind(SpeechDetector.class).in(Singleton.class);

    bind(SwingWindow.class).in(Singleton.class);
    bind(DroneInputController.class).in(Singleton.class);
  }
}