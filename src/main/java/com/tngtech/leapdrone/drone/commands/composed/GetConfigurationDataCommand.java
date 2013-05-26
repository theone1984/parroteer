package com.tngtech.leapdrone.drone.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.simple.ControlDataATCommand;
import com.tngtech.leapdrone.drone.data.enums.ControlDataMode;

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