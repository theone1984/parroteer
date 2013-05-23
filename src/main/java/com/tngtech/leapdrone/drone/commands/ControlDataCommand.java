package com.tngtech.leapdrone.drone.commands;

public class ControlDataCommand extends CommandAbstract
{
  public enum ControlDataMode
  {
    IDLE(0),
    GET_CONFIGURATION_DATA(4),
    RESET_ACK_FLAG(5);

    private final int controlModeCode;

    ControlDataMode(int controlModeCode)
    {
      this.controlModeCode = controlModeCode;
    }

    private int getControlModeCode()
    {
      return controlModeCode;
    }
  }

  private final ControlDataMode controlDataMode;

  public ControlDataCommand(ControlDataMode controlDataMode)
  {
    super(false);
    this.controlDataMode = controlDataMode;
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    return String.format("AT*CTRL=%d,%d,0", sequenceNumber, controlDataMode.getControlModeCode());
  }
}
