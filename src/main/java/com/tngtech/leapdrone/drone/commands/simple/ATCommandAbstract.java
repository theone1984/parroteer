package com.tngtech.leapdrone.drone.commands.simple;

import com.tngtech.leapdrone.drone.CommandSender;
import com.tngtech.leapdrone.drone.CommandSenderCoordinator;
import com.tngtech.leapdrone.drone.commands.ATCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;

public abstract class ATCommandAbstract implements ATCommand
{
  private static final String CARRIAGE_RETURN = "\r";

  private static final int NO_TIMEOUT = 0;

  private final boolean preparationCommandNeeded;

  public ATCommandAbstract(boolean preparationCommandNeeded)
  {
    this.preparationCommandNeeded = preparationCommandNeeded;
  }

  @Override
  public void execute(CommandSender commandSender, CommandSenderCoordinator commandSenderCoordinator)
  {
    commandSender.sendCommand(this);
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

  @Override
  public int getTimeoutMillis()
  {
    // Overwrite if something different is needed
    return NO_TIMEOUT;
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    // Overwrite if something different is needed
  }
}