package com.tngtech.internal.droneapi.commands;

import com.tngtech.internal.droneapi.CommandSender;
import com.tngtech.internal.droneapi.CommandSenderCoordinator;

public interface SimpleCommand extends Command
{
  void execute(CommandSender commandSender, CommandSenderCoordinator commandSenderCoordinator);
}