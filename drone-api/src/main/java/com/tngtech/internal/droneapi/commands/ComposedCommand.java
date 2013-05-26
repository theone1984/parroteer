package com.tngtech.internal.droneapi.commands;

import java.util.Collection;

public interface ComposedCommand extends Command
{
  Collection<Command> getCommands();
}
