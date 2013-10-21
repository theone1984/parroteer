package com.dronecontrol.droneapi;

import com.google.inject.Inject;
import com.dronecontrol.droneapi.commands.Command;
import com.dronecontrol.droneapi.commands.ComposedCommand;
import com.dronecontrol.droneapi.commands.SimpleCommand;
import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.NavData;
import com.dronecontrol.droneapi.listeners.DroneConfigurationListener;
import com.dronecontrol.droneapi.listeners.NavDataListener;
import org.apache.log4j.Logger;

import static com.google.common.base.Preconditions.checkState;
import static com.dronecontrol.droneapi.helpers.ThreadHelper.sleep;

public class CommandSenderCoordinator implements NavDataListener, DroneConfigurationListener
{
  private static final int MAX_RETRIES = 5;

  private final Logger logger = Logger.getLogger(CommandSenderCoordinator.class);

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
    int currentTry = 0;
    do
    {
      command.execute(commandSender, this);
      if (checkSuccessful(command, currentTry))
      {
        break;
      }
      currentTry++;
    } while (true);
  }

  private void executeComposedCommand(ComposedCommand command)
  {
    int currentTry = 0;
    do
    {
      executeAllSubCommands(command);
      if (checkSuccessful(command, currentTry))
      {
        break;
      }
      currentTry++;
    } while (true);
  }

  private void executeAllSubCommands(ComposedCommand command)
  {
    for (Command subCommand : command.getCommands())
    {
      executeCommand(subCommand);
    }
  }

  private boolean checkSuccessful(Command command, int currentTry)
  {
    waitFor(command);

    try
    {
      command.checkSuccess(currentNavData, currentDroneConfiguration);
      return true;
    } catch (Exception e)
    {
      checkCurrentTry(currentTry, e);
      logger.debug(String.format("Command check failed: %s", e.getMessage()));
      return false;
    }
  }

  private void checkCurrentTry(int currentTry, Exception e)
  {
    checkState(currentTry <= MAX_RETRIES, "A check operation was not successful: " + e.getMessage());
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