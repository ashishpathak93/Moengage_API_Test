package com.moengage.apitest.base;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import com.aventstack.extentreports.ExtentReports;
import com.moengage.apitest.log.AutoLogger;
import com.moengage.apitest.log.CustomLogger;
import com.moengage.apitest.report.ExtentManager;

public abstract class BaseClass {

  private static ExtentReports extentReports = ExtentManager.getInstance();
  static CustomLogger logger = AutoLogger.getAutoLogger();

  /**
   * @return logger
   */
  public static CustomLogger getLogger() {
    return logger;
  }

  /**
   * Teardown steps after every test
   * 
   * @author ashishpathak
   * @param result void
   * @throws Exception
   */
  @AfterMethod
  protected void afterMethod(ITestResult result) throws Exception {
    extentReports.flush();
  }

}