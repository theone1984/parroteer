package com.dronecontrol.droneapi.commands;

import com.dronecontrol.droneapi.CommandSender;
import com.dronecontrol.droneapi.CommandSenderCoordinator;

public interface SimpleCommand extends Command
{
  void execute(CommandSender commandSender, CommandSenderCoordinator commandSenderCoordinator);
}