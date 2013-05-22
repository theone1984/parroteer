package com.tngtech.leapdrone.drone.commands;

public class WatchDogCommand extends CommandAbstract
{
  public WatchDogCommand()
  {
    super(false);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*COMWDG=%d", sequenceNumber);
  }
}
