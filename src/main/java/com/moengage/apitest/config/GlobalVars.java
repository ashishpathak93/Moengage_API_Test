package com.moengage.apitest.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.moengage.apitest.utils.SingletonManager;

public class GlobalVars {

  public static final String SHEETNAME = System.getProperty("sheetname");
  public static final String ENVIRONMENT = System.getProperty("environment");
  public static final String user_dir = System.getProperty("user.dir");

  public static final String property_dir = user_dir + File.separator + "resources" + File.separator;
  public static final String extentReport = "test-output" + File.separator + "html" + File.separator + "Extent.html";

  public static final String REPORTPATH = SingletonManager.getPropertiesReader().getProperty("extent.report.path");
  public static final String REPORTNAME = SingletonManager.getPropertiesReader().getProperty("extent.report.name");
  public static final String LOGPATH = SingletonManager.getPropertiesReader().getProperty("extent.log.path");

  public static final String PUBLICKEY = SingletonManager.getPropertiesReader().getProperty("public.key");
  public static final String PRIVATEKEY = SingletonManager.getPropertiesReader().getProperty("private.key");

  public static final String propertyFile = SingletonManager.getPropertiesReader().getProperty("app.datafile.name")
      .length() > 1 ? SingletonManager.getPropertiesReader().getProperty("app.datafile.name")
          : "application.properties";

  public static final HashMap<String, String> RESPONSEMAP = new HashMap<String, String>();

  public static final List<String> TC_HEADERS = Arrays
      .asList(SingletonManager.getPropertiesReader().getProperty("testcase.header").split(","));

  public static int RETRYFAILEDTIMES = Integer
      .parseInt(SingletonManager.getPropertiesReader().getProperty("testng.retry.count"));

  public static int RETRYFAILEDMAXTIME = Integer
      .parseInt(SingletonManager.getPropertiesReader().getProperty("testng.retry.maxtime"));

  public static final String NEXTLINE = "\\r?\\n";
  public static final String COLON = ":";
}
