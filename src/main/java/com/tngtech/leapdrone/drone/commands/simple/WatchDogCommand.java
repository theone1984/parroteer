package com.tngtech.leapdrone.drone.commands.simple;

public class WatchDogCommand extends ATCommandAbstract
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
