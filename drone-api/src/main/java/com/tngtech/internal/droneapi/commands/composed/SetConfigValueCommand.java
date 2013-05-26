package com.tngtech.internal.droneapi.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.internal.droneapi.commands.Command;
import com.tngtech.internal.droneapi.commands.simple.ControlDataATCommand;
import com.tngtech.internal.droneapi.commands.simple.SetConfigValueATCommand;
import com.tngtech.internal.droneapi.data.LoginData;
import com.tngtech.internal.droneapi.data.enums.ControlDataMode;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

public class SetConfigValueCommand extends UnconditionalComposedCommandAbstract
{
  private final LoginData loginData;

  private final String key;

  private final Object value;

  protected SetConfigValueCommand(LoginData loginData)
  {
    this.loginData = loginData;
    this.key = null;
    this.value = null;
  }

  public SetConfigValueCommand(LoginData loginData, String key, Object value)
  {
    this.loginData = loginData;
    this.key = key;
    this.value = value;
  }

  @Override
  public Collection<Command> getCommands()
  {
    Command resetAckFlagCommand = new ControlDataATCommand(ControlDataMode.RESET_ACK_FLAG);
    Command setConfigValueCommand = getConfigValueCommand();

    return Lists.newArrayList(resetAckFlagCommand, setConfigValueCommand);
  }

  protected Command getConfigValueCommand()
  {
    checkState(key != null && value != null, "Key and value must be set");
    return new SetConfigValueATCommand(loginData, key, value);
  }

  protected LoginData getLoginData()
  {
    return loginData;
  }
}