package com.tngtech.leapdrone.input.speech.data;

public class SpeechData
{
  private final String sentence;

  public SpeechData(String sentence)
  {
    this.sentence = sentence;
  }

  public String getSentence()
  {
    return sentence;
  }
}