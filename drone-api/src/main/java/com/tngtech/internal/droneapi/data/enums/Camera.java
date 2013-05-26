package com.tngtech.internal.droneapi.data.enums;

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
