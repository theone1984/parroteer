package com.tngtech.leapdrone.drone.commands;

public abstract class CommandAbstract implements Command
{
  private static final String CARRIAGE_RETURN = "\r";

  private final boolean preparationCommandNeeded;

  public CommandAbstract(boolean preparationCommandNeeded)
  {
    this.preparationCommandNeeded = preparationCommandNeeded;
  }

  @Override
  public String getPreparationCommandText(int sequenceNumber)
  {
    return getPreparationCommand(sequenceNumber) == null ? null : getPreparationCommand(sequenceNumber) + CARRIAGE_RETURN;
  }

  @Override
  public String getCommandText(int sequenceNumber)
  {
    return getCommand(sequenceNumber) + CARRIAGE_RETURN;
  }

  protected abstract String getCommand(int sequenceNumber);

  protected String getPreparationCommand(int sequenceNumber)
  {
    return null;
  }

  @Override
  public boolean isPreparationCommandNeeded()
  {
    return preparationCommandNeeded;
  }
}