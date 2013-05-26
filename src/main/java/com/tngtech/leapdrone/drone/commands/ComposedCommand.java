package com.tngtech.leapdrone.drone.commands;

import java.util.Collection;

public interface ComposedCommand extends Command
{
  Collection<Command> getCommands();
}
