package com.tngtech.leapdrone.input.speech.listeners;

import com.tngtech.leapdrone.input.speech.data.SpeechData;

public interface SpeechListener
{
  public void onSpeech(SpeechData speechData);
}
