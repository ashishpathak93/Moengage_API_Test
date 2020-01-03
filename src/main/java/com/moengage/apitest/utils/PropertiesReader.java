package com.moengage.apitest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

  Properties properties;
  String fileName = System.getProperty("user.dir") + File.separator + "resources" + File.separator
      + "application.properties";

  public PropertiesReader() {
    properties = new Properties();
    try {
      properties.load(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key).trim();
  }

  public void setProperty(String key, String value) {
    properties.setProperty(key, value);
  }
}
