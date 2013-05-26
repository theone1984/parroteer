package com.tngtech.leapdrone.drone.commands.simple;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;
import com.tngtech.leapdrone.drone.data.NavData;

import static com.google.common.base.Preconditions.checkState;

public class SetConfigValueATCommand extends ATCommandAbstract
{
  private final LoginData loginData;

  private final String key;

  private final String value;

  protected SetConfigValueATCommand(LoginData loginData)
  {
    super(true);
    this.loginData = loginData;
    this.key = null;
    this.value = null;
  }

  public SetConfigValueATCommand(LoginData loginData, String key, Object value)
  {
    super(true);
    this.loginData = loginData;
    this.key = key;
    this.value = value.toString();
  }

  @Override
  protected String getPreparationCommand(int sequenceNumber)
  {
    return String.format("AT*CONFIG_IDS=%d,\"%s\",\"%s\",\"%s\"", sequenceNumber, loginData.getSessionChecksum(), loginData.getProfileChecksum(),
            loginData.getApplicationChecksum());
  }

  @Override
  protected String getCommand(int sequenceNumber)
  {
    checkState(key != null && value != null, "Cannot get the command text with no key or value set");
    return String.format("AT*CONFIG=%d,\"%s\",\"%s\"", sequenceNumber, key, value);
  }

  @Override
  public int getTimeoutMillis()
  {
    return DEFAULT_NAVDATA_TIMEOUT;
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    checkState(navData.getState().isControlReceived(), "The command config ACK flag was not set");
  }
}