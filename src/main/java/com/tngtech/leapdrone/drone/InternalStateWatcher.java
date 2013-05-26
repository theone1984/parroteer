package com.tngtech.leapdrone.drone;

import com.google.common.collect.Lists;
import com.tngtech.leapdrone.drone.commands.ATCommand;
import com.tngtech.leapdrone.drone.commands.simple.FlatTrimCommand;
import com.tngtech.leapdrone.drone.commands.simple.FlightModeCommand;
import com.tngtech.leapdrone.drone.data.InternalState;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.data.enums.FlightMode;
import com.tngtech.leapdrone.drone.listeners.NavDataListener;

import javax.inject.Inject;
import java.util.Collection;

public class InternalStateWatcher implements NavDataListener
{
  private InternalState internalState;

  private NavData currentNavData;

  @Inject
  public InternalStateWatcher(NavigationDataRetriever navigationDataRetriever)
  {
    navigationDataRetriever.addNavDataListener(this);
    internalState = new InternalState();
  }

  public Collection<ATCommand> getCommandsToUpholdInternalState()
  {
    if (currentNavData == null)
    {
      return Lists.newArrayList();
    }

    Collection<ATCommand> commands = Lists.newArrayList();
    addNecessaryCommands(commands);
    resetState();

    return commands;
  }

  private void addNecessaryCommands(Collection<ATCommand> commands)
  {
    if (internalState.isTakeOffRequested() && !currentNavData.getState().isFlying())
    {
      commands.add(new FlightModeCommand(FlightMode.TAKE_OFF));
    }
    if (internalState.isLandRequested() && currentNavData.getState().isFlying())
    {
      commands.add(new FlightModeCommand(FlightMode.LAND));
    }
    if (internalState.isEmergencyRequested() && currentNavData.getState().isEmergency())
    {
      commands.add(new FlightModeCommand(FlightMode.LAND));
    }
    if (internalState.isFlatTrimRequested())
    {
      commands.add(new FlatTrimCommand());
    }
  }

  private void resetState()
  {
    // Flat trim is a one-off command, so it is reset here
    internalState.setFlatTrimRequested(false);

    // Emergency state may reset itself
    // If it is set in nav data, there is no need for further checks
    if (internalState.isEmergencyRequested() && currentNavData.getState().isEmergency())
    {
      internalState.setEmergencyRequested(false);
    }

    // Flying and landing states can be reset whenever the requested state occurs
    if (internalState.isTakeOffRequested() && currentNavData.getState().isFlying())
    {
      internalState.setTakeOffRequested(false);
    }
    if (internalState.isLandRequested() && !currentNavData.getState().isFlying())
    {
      internalState.setLandRequested(false);
    }
  }

  public void requestTakeOff()
  {
    internalState.setTakeOffRequested(true);
    internalState.setLandRequested(false);
  }

  public void requestLand()
  {
    internalState.setLandRequested(true);
    internalState.setTakeOffRequested(false);
  }

  public void requestEmergency()
  {
    internalState.setEmergencyRequested(true);
  }

  public void requestFlatTrim()
  {
    internalState.setFlatTrimRequested(true);
  }

  @Override
  public void onNavData(NavData navData)
  {
    currentNavData = navData;
  }
}