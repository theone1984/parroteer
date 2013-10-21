package com.dronecontrol.droneapi.commands.composed;

import com.dronecontrol.droneapi.data.DroneConfiguration;
import com.dronecontrol.droneapi.data.LoginData;
import com.dronecontrol.droneapi.data.enums.Camera;

public class SwitchCameraCommand extends SetConfigValueCommand
{
  public SwitchCameraCommand(LoginData loginData, Camera camera)
  {
    super(loginData, DroneConfiguration.VIDEO_CHANNEL_KEY, camera.getCameraCode());
  }
}