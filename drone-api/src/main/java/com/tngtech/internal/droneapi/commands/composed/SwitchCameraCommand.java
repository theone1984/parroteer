package com.tngtech.internal.droneapi.commands.composed;

import com.tngtech.internal.droneapi.data.DroneConfiguration;
import com.tngtech.internal.droneapi.data.LoginData;
import com.tngtech.internal.droneapi.data.enums.Camera;

public class SwitchCameraCommand extends SetConfigValueCommand
{
  public SwitchCameraCommand(LoginData loginData, Camera camera)
  {
    super(loginData, DroneConfiguration.VIDEO_CHANNEL_KEY, camera.getCameraCode());
  }
}