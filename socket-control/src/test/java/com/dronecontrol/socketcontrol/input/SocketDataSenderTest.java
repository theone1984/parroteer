package com.dronecontrol.socketcontrol.input;

import com.dronecontrol.droneapi.data.NavData;
import com.dronecontrol.droneapi.data.NavDataState;
import com.dronecontrol.socketcontrol.input.socket.SocketClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SocketDataSenderTest
{
  @Mock
  private SocketClient socketClient;

  @Mock
  private NavData navData;

  @Mock
  private NavDataState navDataState;

  private SocketDataSender socketDataSender;

  @Before
  public void setUp()
  {
    when(navData.getState()).thenReturn(navDataState);
    when(socketClient.isConnected()).thenReturn(true);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonFactory jsonFactory = new JsonFactory();

    socketDataSender = new SocketDataSender(socketClient, jsonFactory, objectMapper);
  }

  @Test
  public void testOnNavData()
  {
    mockNavData(true, 0.5f);
    socketDataSender.onNavData(navData);

    verify(socketClient).send("{\"flying\":true,\"currentHeight\":0.5}");
  }

  @Test
  public void testOnNavDataWithLowAltitude()
  {
    mockNavData(true, 0.1f);
    socketDataSender.onNavData(navData);

    verify(socketClient).send("{\"flying\":true,\"currentHeight\":0.0}");
  }

  private void mockNavData(boolean flying, float altitude)
  {
    when(navDataState.isFlying()).thenReturn(flying);
    when(navData.getAltitude()).thenReturn(altitude);
  }
}