package com.tngtech.leapdrone.drone.data;

public enum DroneControllerState
{
  STARTED,
  COMMAND_ONE_RETRIEVER_READY,
  COMMAND_TWO_RETRIEVERS_READY,
  WORKERS_READY,
  READY,
  STOPPED
}
