package com.dronecontrol.droneapi.helpers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChecksumHelperTest
{
  @Test
  public void testCreateCrc32Hex()
  {
    assertThat(ChecksumHelper.createCrc32Hex("value"), is("1d775834"));
  }
}
