package com.tngtech.leapdrone.drone;

import com.tngtech.leapdrone.drone.components.ThreadComponent;
import com.tngtech.leapdrone.drone.components.UdpComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CommandSenderTest
{
  @Mock
  private ThreadComponent threadComponent;

  @Mock
  private UdpComponent udpComponent;

  private CommandSender commandSender;

  @Before
  public void setUp()
  {
    commandSender = new CommandSender(threadComponent, udpComponent);
  }

  @Test
  public void testConversion()
  {
    int intValue = commandSender.normalizeValue(-0.8f);
    assertThat(intValue, is(-1085485875));
  }
}