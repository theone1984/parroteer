package com.tngtech.leapdrone.drone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DroneCommunicator
{
  private final static int TAKE_OFF_VALUE = 290718208;

  private static final int LAND_VALUE = 290717696;

  Socket socket;

  private BufferedReader socketReader;

  private PrintWriter socketWriter;

  private int sequenceNumber = 0;

  public void connect()
  {
    try
    {
      socket = new Socket("localhost", 5554);
      socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      socketWriter = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void sendTakeOff()
  {
    sendFlightModeCommand(TAKE_OFF_VALUE);
  }

  public void sendLand()
  {
    sendFlightModeCommand(LAND_VALUE);
  }

  private void sendFlightModeCommand(int flightModeValue)
  {
    String command = String.format("AT*REF=%s,%s\r", sequenceNumber, flightModeValue);
    System.out.println(command);
    socketWriter.println(command);
  }
}