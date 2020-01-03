package com.moengage.apitest.report;

import java.io.File;
import java.io.IOException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.moengage.apitest.config.GlobalVars;

public class ExtentManager {
  public static final String REPORT_NAME = GlobalVars.user_dir + File.separatorChar + GlobalVars.extentReport;
  private static ExtentReports extent;

  public static ExtentReports getInstance() {
    if (extent == null)
      createInstance();
    return extent;
  }

  private static ExtentReports createInstance() {
    ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(getEnvironmentPath());
    htmlReporter.config().setTheme(Theme.DARK);
    htmlReporter.config().setDocumentTitle("Test Suite Reports");
    htmlReporter.config().setEncoding("utf-8");
    htmlReporter.config().setReportName("Moengage  Test  Reports");
    extent = new ExtentReports();
    extent.attachReporter(htmlReporter);
    return extent;
  }

  public static File getEnvironmentPath() {
    String currentDir = null;
    try {
      currentDir = new File(".").getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    File file = new File(REPORT_NAME);
    return file;
  }
}