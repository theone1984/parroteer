package com.tngtech.leapdrone.drone;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DroneCommunicatorTest
{
  private DroneCommunicator droneCommunicator;

  @Before
  public void setUp()
  {
    droneCommunicator = new DroneCommunicator();
  }

  @Test
  public void testConversion()
  {
    int intValue = droneCommunicator.normalizeValue(-0.8f);
    assertThat(intValue, is(-1085485875));
  }
}