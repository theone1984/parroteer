package com.dronecontrol.droneapi.commands;

import java.util.Collection;

public interface ComposedCommand extends Command
{
  Collection<Command> getCommands();
}
