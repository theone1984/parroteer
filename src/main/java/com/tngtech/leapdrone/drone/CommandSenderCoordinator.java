package com.tngtech.leapdrone.drone;

import com.google.inject.Inject;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.ComposedCommand;
import com.tngtech.leapdrone.drone.commands.SimpleCommand;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.listeners.DroneConfigurationListener;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;
import org.apache.log4j.Logger;

import static com.tngtech.leapdrone.drone.helpers.ThreadHelper.sleep;

public class CommandSenderCoordinator implements NavDataListener, DroneConfigurationListener
{
  private final Logger logger = Logger.getLogger(CommandSenderCoordinator.class.getSimpleName());

  private final CommandSender commandSender;

  private NavData currentNavData;

  private DroneConfiguration currentDroneConfiguration;

  @Inject
  public CommandSenderCoordinator(CommandSender commandSender, NavigationDataRetriever navigationDataRetriever,
                                  ConfigurationDataRetriever configurationDataRetriever)
  {
    this.commandSender = commandSender;
    navigationDataRetriever.addNavDataListener(this);
    configurationDataRetriever.addDroneConfigurationListener(this);
  }

  public void executeCommand(Command command)
  {
    if (command instanceof SimpleCommand)
    {
      executeSimpleCommand((SimpleCommand) command);
    } else if (command instanceof ComposedCommand)
    {
      executeComposedCommand((ComposedCommand) command);
    }
  }

  private void executeSimpleCommand(SimpleCommand command)
  {
    do
    {
      command.execute(commandSender, this);
      if (checkSuccessful(command))
      {
        break;
      }
    } while (true);
  }

  private void executeComposedCommand(ComposedCommand command)
  {
    do
    {
      executeAllSubCommands(command);
      if (checkSuccessful(command))
      {
        break;
      }
    } while (true);
  }

  private void executeAllSubCommands(ComposedCommand command)
  {
    for (Command subCommand : command.getCommands())
    {
      executeCommand(subCommand);
    }
  }

  private boolean checkSuccessful(Command command)
  {
    waitFor(command);

    try
    {
      command.checkSuccess(currentNavData, currentDroneConfiguration);
      return true;
    } catch (Exception e)
    {
      logger.debug(String.format("Command check failed: %s", e.getMessage()));
      return false;
    }
  }

  private void waitFor(Command command)
  {
    if (command.getTimeoutMillis() != Command.NO_TIMEOUT)
    {
      sleep(command.getTimeoutMillis());
    }
  }

  public void resetConfiguration()
  {
    currentDroneConfiguration = null;
  }

  @Override
  public void onDroneConfiguration(DroneConfiguration configuration)
  {
    currentDroneConfiguration = configuration;
  }

  @Override
  public void onNavData(NavData navData)
  {
    currentNavData = navData;
  }
}