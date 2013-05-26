package com.tngtech.internal.leapcontrol.input.speech.listeners;

import com.tngtech.internal.leapcontrol.input.speech.data.SpeechData;

public interface SpeechListener
{
  public void onSpeech(SpeechData speechData);
}
