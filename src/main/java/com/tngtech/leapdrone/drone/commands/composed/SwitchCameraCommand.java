package com.tngtech.leapdrone.drone.commands.composed;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;
import com.tngtech.leapdrone.drone.data.enums.Camera;

public class SwitchCameraCommand extends SetConfigValueCommand
{
  public SwitchCameraCommand(LoginData loginData, Camera camera)
  {
    super(loginData, DroneConfiguration.VIDEO_CHANNEL_KEY, camera.getCameraCode());
  }
}