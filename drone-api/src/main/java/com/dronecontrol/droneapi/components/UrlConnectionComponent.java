package com.dronecontrol.droneapi.components;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class UrlConnectionComponent
{
  private URLConnection connection;

  private InputStream inputStream;

  private BufferedReader reader;

  public void connect(String urlPath)
  {
    try
    {
      URL url = new URL(urlPath);
      connection = url.openConnection();
      inputStream = connection.getInputStream();
      reader = new BufferedReader(new InputStreamReader(inputStream));
    } catch (IOException e)
    {
      throw new IllegalStateException(String.format("Error while connecting to url '%s'", urlPath), e);
    }
  }

  public void disconnect()
  {
    try
    {
      reader.close();
      inputStream.close();
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while disconnecting", e);
    }
  }

  public Collection<String> readLines()
  {
    try
    {
      return doReadLines();
    } catch (IOException e)
    {
      throw new IllegalStateException("Error while reading lines", e);
    }
  }

  private Collection<String> doReadLines() throws IOException
  {
    Collection<String> lines = Lists.newArrayList();
    String line = reader.readLine();
    while (line != null)
    {
      lines.add(line);
      line = reader.readLine();
    }

    return lines;
  }
}
