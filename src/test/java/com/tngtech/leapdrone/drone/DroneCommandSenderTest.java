package com.tngtech.leapdrone.drone;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DroneCommandSenderTest
{
  private DroneCommandSender droneCommandSender;

  @Before
  public void setUp()
  {
    droneCommandSender = new DroneCommandSender();
  }

  @Test
  public void testConversion()
  {
    int intValue = droneCommandSender.normalizeValue(-0.8f);
    assertThat(intValue, is(-1085485875));
  }
}