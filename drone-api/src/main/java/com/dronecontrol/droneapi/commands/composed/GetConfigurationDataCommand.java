package com.dronecontrol.droneapi.commands.composed;

import com.google.common.collect.Lists;
import com.dronecontrol.droneapi.commands.Command;
import com.dronecontrol.droneapi.commands.simple.ControlDataATCommand;
import com.dronecontrol.droneapi.data.enums.ControlDataMode;

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