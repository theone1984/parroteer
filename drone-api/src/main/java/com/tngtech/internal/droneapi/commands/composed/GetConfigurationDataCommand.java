package com.tngtech.internal.droneapi.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.internal.droneapi.commands.Command;
import com.tngtech.internal.droneapi.commands.simple.ControlDataATCommand;
import com.tngtech.internal.droneapi.data.enums.ControlDataMode;

import java.util.Collection;

public class GetConfigurationDataCommand extends UnconditionalComposedCommandAbstract
{
  @Override
  public Collection<Command> getCommands()
  {
    Command resetAckFlagCommand = new ControlDataATCommand(ControlDataMode.RESET_ACK_FLAG);
    Command getConfigurationDataCommand = new ControlDataATCommand(ControlDataMode.GET_CONFIGURATION_DATA);

    return Lists.newArrayList(resetAckFlagCommand, getConfigurationDataCommand);
  }
}