package com.tngtech.leapdrone.drone.commands;

public class WatchDogCommand extends CommandAbstract
{
  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*COMWDG=%d", sequenceNumber);
  }
}
