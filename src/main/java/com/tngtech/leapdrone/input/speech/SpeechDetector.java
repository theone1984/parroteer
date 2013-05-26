package com.tngtech.leapdrone.input.speech;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.injection.Context;
import com.tngtech.leapdrone.input.speech.data.SpeechData;
import com.tngtech.leapdrone.input.speech.listeners.SpeechListener;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import org.apache.log4j.Logger;

import java.util.Set;

public class SpeechDetector implements Runnable
{
  private static final String CONFIG_FILE_PATH = "command.config.xml";

  private static final String RECOGNIZER = "recognizer";

  private static final String MICROPHONE = "microphone";

  private final Logger logger = Logger.getLogger(SpeechDetector.class);

  private final ThreadComponent threadComponent;

  private Set<SpeechListener> speechListeners;

  private Recognizer recognizer;

  private Microphone microphone;

  public static void main(String[] args)
  {
    SpeechDetector speechDetector = Context.getBean(SpeechDetector.class);
    speechDetector.start();
  }

  @Inject
  public SpeechDetector(ThreadComponent threadComponent)
  {
    this.threadComponent = threadComponent;
    speechListeners = Sets.newHashSet();

    initializeDetection();
  }

  private void initializeDetection()
  {
    logger.info("Initializing speech detection");
    ConfigurationManager configurationManager = new ConfigurationManager(SpeechDetector.class.getResource(CONFIG_FILE_PATH));
    recognizer = (Recognizer) configurationManager.lookup(RECOGNIZER);
    microphone = (Microphone) configurationManager.lookup(MICROPHONE);
    recognizer.allocate();
  }

  public void start()
  {
    logger.info("Starting speech recognition thread");
    threadComponent.start(this);
  }

  public void stop()
  {
    logger.info("Ending speech recognition thread");
    threadComponent.stop();

    microphone.stopRecording();
    recognizer.deallocate();
  }

  @Override
  public void run()
  {
    logger.info("Starting speech recognition");
    startMicrophone();

    while (!threadComponent.isStopped())
    {
      try
      {
        Result result = recognizer.recognize();

        if (result != null)
        {
          processResult(result);
        }
      } catch (RuntimeException e)
      {
        // This happens when the recognizer is deallocated (which only happens when the stop() method was called)
      }
    }

    logger.info("Ending speech recognition");
  }

  private void startMicrophone()
  {
    if (!microphone.startRecording())
    {
      stop();
      throw new IllegalStateException("Cannot start microphone");
    }

    logger.info("Microphone is now listening");
  }

  private void processResult(Result result)
  {
    SpeechData speechData = getSpeechData(result);

    if (speechData == null)
    {
      return;
    }

    logger.debug(String.format("Recognized speech input '%s'", speechData.getSentence()));
    for (SpeechListener listener : speechListeners)
    {
      listener.onSpeech(speechData);
    }
  }

  private SpeechData getSpeechData(Result result)
  {
    String resultText = result.getBestFinalResultNoFiller();

    if ("".equals(resultText))
    {
      return null;
    }
    return new SpeechData(resultText);
  }

  public void addSpeechListener(SpeechListener speechListener)
  {
    if (!speechListeners.contains(speechListener))
    {
      speechListeners.add(speechListener);
    }
  }

  public void removeSpeechListener(SpeechListener speechListener)
  {
    if (speechListeners.contains(speechListener))
    {
      speechListeners.remove(speechListener);
    }
  }
}