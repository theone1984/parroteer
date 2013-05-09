package com.tngtech.leapdrone.helpers;

public class BinaryDataHelper
{
  public static int getInt(byte[] data, int offset, int length)
  {
    int tempValue;
    int integerValue = 0;

    for (int i = length - 1; i >= 0; i--)
    {
      integerValue <<= 8;
      tempValue = data[offset + i] & 0xFF;
      integerValue |= tempValue;
    }

    return integerValue;
  }
}
