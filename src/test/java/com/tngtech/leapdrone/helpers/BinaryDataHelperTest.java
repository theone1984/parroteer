package com.tngtech.leapdrone.helpers;

import com.tngtech.leapdrone.drone.helpers.BinaryDataHelper;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BinaryDataHelperTest
{
  @Test
  public void testConversion()
  {
    int intValue = BinaryDataHelper.getNormalizedIntValue(-0.8f);
    assertThat(intValue, is(-1085485875));
  }
}