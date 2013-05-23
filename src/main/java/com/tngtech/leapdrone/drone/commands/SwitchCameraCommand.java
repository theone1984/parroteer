package com.tngtech.leapdrone.drone.commands;

import com.tngtech.leapdrone.drone.data.DroneConfiguration;

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

  public SwitchCameraCommand(Camera camera)
  {
    super(DroneConfiguration.VIDEO_CHANNEL_KEY, camera.getCameraCode());
  }
}