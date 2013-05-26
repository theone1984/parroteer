package com.tngtech.leapdrone.drone.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;

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
