package com.tngtech.leapdrone.drone.commands.simple;

import com.tngtech.leapdrone.drone.CommandSender;
import com.tngtech.leapdrone.drone.CommandSenderCoordinator;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.data.enums.ControlDataMode;

import static com.google.common.base.Preconditions.checkState;

public class ControlDataATCommand extends ATCommandAbstract
{
  private final ControlDataMode controlDataMode;

  public ControlDataATCommand(ControlDataMode controlDataMode)
  {
    super(false);
    this.controlDataMode = controlDataMode;
  }

  @Override
  public void execute(CommandSender commandSender, CommandSenderCoordinator commandSenderCoordinator)
  {
    if (controlDataMode == ControlDataMode.GET_CONFIGURATION_DATA)
    {
      commandSenderCoordinator.resetConfiguration();
    }
    super.execute(commandSender, commandSenderCoordinator);
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CTRL=%d,%d,0", sequenceNumber, controlDataMode.getControlModeCode());
  }

  @Override
  public int getTimeoutMillis()
  {
    switch (controlDataMode)
    {
      case RESET_ACK_FLAG:
        return DEFAULT_NAVDATA_TIMEOUT;
      case GET_CONFIGURATION_DATA:
        return DEFAULT_CONFIGURATION_TIMEOUT;
      default:
        return 0;
    }
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    switch (controlDataMode)
    {
      case RESET_ACK_FLAG:
        checkState(!navData.getState().isControlReceived(), "The command config ACK flag was not reset");
        break;
      case GET_CONFIGURATION_DATA:
        checkState(droneConfiguration != null, "The drone configuration was not sent");
        break;
    }
  }
}