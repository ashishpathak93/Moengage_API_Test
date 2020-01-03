package com.moengage.apitest.utils;

public class SingletonManager {

  private static PropertiesReader propertiesReader;

  public static PropertiesReader getPropertiesReader() {

    if (propertiesReader == null) {
      propertiesReader = new PropertiesReader();
    }
    return propertiesReader;
  }

  public static PropertiesReader setPropertiesReader() {

    propertiesReader = new PropertiesReader();
    return propertiesReader;
  }
}
