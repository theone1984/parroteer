package com.tngtech.internal.droneapi.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.internal.droneapi.commands.Command;
import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.LoginData;

import java.util.Collection;

public class LoginCommand extends UnconditionalComposedCommandAbstract
{
  private final LoginData loginData;

  public LoginCommand(LoginData loginData)
  {
    this.loginData = loginData;
  }

  @Override
  public Collection<Command> getCommands()
  {
    Command setSessionIdComamnd = new SetConfigValueCommand(loginData, DroneConfiguration.SESSION_ID_KEY, loginData.getSessionChecksum());
    Command setProfileIdCommand = new SetConfigValueCommand(loginData, DroneConfiguration.PROFILE_ID_KEY, loginData.getProfileChecksum());
    Command setApplicationIdCommand = new SetConfigValueCommand(loginData, DroneConfiguration.APPLICATION_ID_KEY, loginData.getApplicationChecksum());
    return Lists.newArrayList(setSessionIdComamnd, setProfileIdCommand, setApplicationIdCommand);
  }
}
