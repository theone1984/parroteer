package com.tngtech.leapdrone.drone.commands;

public interface Command
{
  String getCommandText(int sequenceNumber);
}