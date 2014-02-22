package com.dronecontrol.socketcontrol.input;

import com.dronecontrol.droneapi.data.NavData;
import com.dronecontrol.droneapi.listeners.NavDataListener;
import com.dronecontrol.socketcontrol.input.data.DroneData;
import com.dronecontrol.socketcontrol.input.socket.SocketClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;

public class SocketDataSender implements NavDataListener
{
  private static final float HEIGHT_THRESHOLD = 0.25f;

  private final SocketClient socketClient;

  private final JsonFactory jsonFactory;

  private final ObjectMapper objectMapper;

  @Inject
  public SocketDataSender(SocketClient socketClient, JsonFactory jsonFactory, ObjectMapper objectMapper)
  {
    this.socketClient = socketClient;
    this.jsonFactory = jsonFactory;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onNavData(NavData navData)
  {
    send(getDroneData(navData));
  }

  private DroneData getDroneData(NavData navData)
  {
    boolean flying = navData.getState().isFlying();
    float currentHeight = getCurrentHeight(navData);
    return new DroneData(flying, currentHeight);
  }

  private float getCurrentHeight(NavData navData)
  {
    return (navData.getAltitude() < HEIGHT_THRESHOLD ? 0.0f : navData.getAltitude());
  }

  private void send(DroneData droneData)
  {
    try
    {
      send(getSerializedJsonText(droneData));
    } catch (IOException e)
    {
      throw new IllegalStateException("Error writing drone data", e);
    }
  }

  private void send(String serializedJsonText)
  {
    if (socketClient.isConnected())
    {
      socketClient.send(serializedJsonText);
    }
  }

  private String getSerializedJsonText(DroneData droneData) throws IOException
  {
    StringWriter stringWriter = new StringWriter();
    JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(stringWriter);

    objectMapper.writeValue(jsonGenerator, droneData);
    return stringWriter.toString();
  }
}