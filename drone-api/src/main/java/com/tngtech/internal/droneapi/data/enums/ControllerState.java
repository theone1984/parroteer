package com.tngtech.internal.droneapi.data.enums;

public enum ControllerState
{
  STARTED,
  COMMAND_ONE_RETRIEVER_READY,
  COMMAND_TWO_RETRIEVERS_READY,
  WORKERS_READY,
  READY,
  STOPPED;

  public ControllerState getNextState()
  {
    switch (this)
    {
      case STARTED:
        return COMMAND_ONE_RETRIEVER_READY;
      case COMMAND_ONE_RETRIEVER_READY:
        return COMMAND_TWO_RETRIEVERS_READY;
      case COMMAND_TWO_RETRIEVERS_READY:
        return WORKERS_READY;
      case WORKERS_READY:
        return READY;
      default:
        throw new IllegalStateException(String.format("There is no follow-up state for state '%s'", this.name()));
    }
  }
}
