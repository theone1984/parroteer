package com.tngtech.leapdrone.drone.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.simple.ControlDataATCommand;

import java.util.Collection;

public class GetConfigurationDataCommand extends UnconditionalComposedCommandAbstract
{
  @Override
  public Collection<Command> getCommands()
  {
    Command resetAckFlagCommand = new ControlDataATCommand(ControlDataATCommand.ControlDataMode.RESET_ACK_FLAG);
    Command getConfigurationDataCommand = new ControlDataATCommand(ControlDataATCommand.ControlDataMode.GET_CONFIGURATION_DATA);

    return Lists.newArrayList(resetAckFlagCommand, getConfigurationDataCommand);
  }
}