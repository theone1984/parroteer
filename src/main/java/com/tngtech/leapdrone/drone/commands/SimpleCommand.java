package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.CommandSender;
import com.tngtech.leapdrone.drone.CommandSenderCoordinator;

public interface SimpleCommand extends Command
{
  void execute(CommandSender commandSender, CommandSenderCoordinator commandSenderCoordinator);
}