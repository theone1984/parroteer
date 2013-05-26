package com.tngtech.leapdrone.drone.commands.composed;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;
import com.tngtech.leapdrone.drone.data.LoginData;

public class SwitchCameraCommand extends SetConfigValueCommand
{
  public enum Camera
  {
    FRONT(0),
    BACK(1),
    PIP_FRONT(2),
    PIP_BACK(3),
    NEXT(4);

    private final int cameraCode;

    Camera(int cameraCode)
    {
      this.cameraCode = cameraCode;
    }

    public int getCameraCode()
    {
      return cameraCode;
    }
  }

  public SwitchCameraCommand(LoginData loginData, Camera camera)
  {
    super(loginData, DroneConfiguration.VIDEO_CHANNEL_KEY, camera.getCameraCode());
  }
}