package com.dronecontrol.droneapi.commands;

public interface ATCommand extends SimpleCommand
{
  String getCommandText(int sequenceNumber);

  String getPreparationCommandText(int sequenceNumber);

  boolean isPreparationCommandNeeded();
}