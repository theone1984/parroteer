package com.tngtech.leapdrone.drone.commands.composed;

import com.google.common.collect.Lists;
import com.tngtech.leapdrone.drone.commands.Command;
import com.tngtech.leapdrone.drone.commands.ComposedCommand;
import com.tngtech.leapdrone.drone.data.Config;
import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;
import com.tngtech.leapdrone.drone.data.NavData;
import com.tngtech.leapdrone.drone.data.enums.ARDrone1VideoCodec;
import com.tngtech.leapdrone.drone.data.enums.ARDrone2VideoCodec;
import com.tngtech.leapdrone.drone.helpers.VersionHelper;

import java.util.Collection;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

public class InitializeConfigurationCommand implements ComposedCommand
{
  private final LoginData loginData;

  private final String videoCodecCode;

  public InitializeConfigurationCommand(LoginData loginData, ARDrone1VideoCodec videoCodec)
  {
    this.loginData = loginData;
    this.videoCodecCode = String.valueOf(videoCodec.getCodecCode());
  }

  public InitializeConfigurationCommand(LoginData loginData, ARDrone2VideoCodec videoCodec)
  {
    this.loginData = loginData;
    this.videoCodecCode = String.valueOf(videoCodec.getCodecCode());
  }

  @Override
  public Collection<Command> getCommands()
  {
    Command loginCommand = new LoginCommand(loginData);
    Command enableNavDataCommand = new EnableNavDataCommand(loginData);
    Command setVideoCodecCommand = new SetConfigValueCommand(loginData, DroneConfiguration.VIDEO_CODEC_KEY, videoCodecCode);
    Command getDroneConfigurationCommand = new GetConfigurationDataCommand();

    return Lists.newArrayList(loginCommand, enableNavDataCommand, setVideoCodecCommand, getDroneConfigurationCommand);
  }

  @Override
  public int getTimeoutMillis()
  {
    return NO_TIMEOUT;
  }

  @Override
  public void checkSuccess(NavData navData, DroneConfiguration droneConfiguration)
  {
    String firmwareVersion = droneConfiguration.getConfig().get(DroneConfiguration.FIRMWARE_VERSION_KEY);
    checkState(VersionHelper.compareVersions(firmwareVersion, Config.MIN_FIRMWARE_VERSION) >= 0, "The firmware version used is too old");

    String sessionId = droneConfiguration.getConfig().get(DroneConfiguration.SESSION_ID_KEY);
    String profileId = droneConfiguration.getConfig().get(DroneConfiguration.PROFILE_ID_KEY);
    String applicationId = droneConfiguration.getConfig().get(DroneConfiguration.APPLICATION_ID_KEY);

    checkState(Objects.equals(loginData.getSessionChecksum(), sessionId),
            String.format("The session ID was not set to '%s', but was '%s'", loginData.getSessionChecksum(), sessionId));
    checkState(Objects.equals(loginData.getProfileChecksum(), profileId), "The profile ID was not set");
    checkState(Objects.equals(loginData.getApplicationChecksum(), applicationId), "The application ID was not set");

    String videoCodecCode = droneConfiguration.getConfig().get(DroneConfiguration.VIDEO_CODEC_KEY);
    checkState(Objects.equals(this.videoCodecCode, videoCodecCode), "The video codec was not set");
  }
}