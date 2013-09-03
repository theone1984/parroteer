package com.dronecontrol.leapcontrol.input.speech.listeners;

import com.dronecontrol.leapcontrol.input.speech.data.SpeechData;

public interface SpeechListener
{
  public void onSpeech(SpeechData speechData);
}
