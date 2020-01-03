package com.moengage.apitest.log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.moengage.apitest.report.ExtentTestManager;

public class CustomLogger {

  private static String FQCN = CustomLogger.class.getName();
  private Logger logger;
  static Map<Integer, ExtentTest> extentTestMap = ExtentTestManager.getExtentTestMap();
  private static Map<Integer, String> testDetailMap = ExtentTestManager.getTestDetailMap();
  Map<String, String> testCaseIDStatusMap = new HashMap<String, String>();

  public CustomLogger(String name) {
    this.logger = Logger.getLogger(name);
  }

  @SuppressWarnings("rawtypes")
  public CustomLogger(Class classZ) {
    this(classZ.getName());
  }

  private ExtentTest getExtentTest() {
    Integer testId = (int) (Thread.currentThread().getId());
    return extentTestMap.get(testId);
  }

  private String getFormatedMessage(Object msg) {
    Integer testId = (int) (Thread.currentThread().getId());
    String testName = testDetailMap.get(testId);
    return testName + " : " + msg.toString();
  }

  private String getTestName() {
    Integer testId = (int) (Thread.currentThread().getId());
    String testName = testDetailMap.get(testId);
    return testName + " : ";
  }

  public void info(Object msg) {
    ExtentTest extentTest = getExtentTest();
    extentTest.log(Status.INFO, getFormatedMessage(msg));
    logger.log(FQCN, CustomLevel.INFO, msg, null);
  }

  public void info(Object msg, Throwable t) {
    logger.log(FQCN, CustomLevel.INFO, msg, t);
    logNestedException(CustomLevel.INFO, msg, t);
  }

  public void pass(Object msg) {
    Markup mgreen = MarkupHelper.createLabel(getFormatedMessage(msg), ExtentColor.GREEN);
    ExtentTest extentTest = getExtentTest();
    extentTest.log(Status.INFO, mgreen);
    logger.log(FQCN, CustomLevel.INFO, msg, null);
    putIntoMap(extentTest, "PASS");

  }

  public void fail(Object msg) {
    Thread.currentThread().getStackTrace();
    Markup mRed = MarkupHelper.createLabel(getFormatedMessage(msg), ExtentColor.RED);
    ExtentTest extentTest = getExtentTest();
    logger.log(FQCN, CustomLevel.ERROR, msg, null);
    extentTest.log(Status.FAIL, mRed);
    putIntoMap(extentTest, "FAIL");
    extentTest.fail(getTestName() + "TEST CASE FAILED");
    Assert.fail(getFormatedMessage(msg));

  }

  public void skip(Object msg) {
    Markup orange = MarkupHelper.createLabel(getFormatedMessage(msg), ExtentColor.ORANGE);
    ExtentTest extentTest = getExtentTest();
    putIntoMap(extentTest, "SKIP");
    extentTest.log(Status.SKIP, orange);
    extentTest.skip(getTestName() + "TEST CASE SKIP");
    Assert.fail(getFormatedMessage(msg));
  }

  public void error(Object msg) {
    logger.log(FQCN, CustomLevel.ERROR, msg, null);
    ExtentTest extentTest = getExtentTest();
    try {
      String testCaseName = extentTest.getModel().getName();
      testCaseName = testCaseName.trim().replaceAll(" ", "");

      putIntoMap(extentTest, "ERROR");
      extentTest.log(Status.ERROR, getFormatedMessage(msg));
      extentTest.fail(getTestName() + "TEST CASE FAILED");

      Assert.fail(getFormatedMessage(msg));
    } catch (Exception e) {
      extentTest.log(Status.FAIL, getFormatedMessage(msg));
      e.printStackTrace();
    }
  }

  public void error(Object msg, Throwable t) {
    logger.log(FQCN, CustomLevel.ERROR, msg, t);
    logNestedException(CustomLevel.ERROR, msg, t);
    ExtentTest extentTest = getExtentTest();
    try {
      String testCaseName = extentTest.getModel().getName();
      testCaseName = testCaseName.trim().replaceAll(" ", "");
      extentTest.log(Status.ERROR, getFormatedMessage(msg));
      extentTest.fail(getTestName() + "TEST CASE FAILED");
      putIntoMap(extentTest, "ERROR");
      Assert.fail(getFormatedMessage(msg));
    } catch (Exception e) {
      extentTest.log(Status.FAIL, getFormatedMessage(msg));
      e.printStackTrace();
    }
  }

  public void warn(Object msg) {
    Markup mgreen = MarkupHelper.createLabel(getFormatedMessage(msg), ExtentColor.LIME);
    ExtentTest extentTest = getExtentTest();
    extentTest.log(Status.INFO, mgreen);
    logger.log(FQCN, CustomLevel.INFO, msg, null);
    putIntoMap(extentTest, "WARN");
  }

  public void putIntoMap(ExtentTest extentTest, String status) {

    String testCaseID = StringUtils.EMPTY;

    for (int k = 0; k < extentTest.getModel().getLogContext().getAll().size(); k++) {
      System.out.println(extentTest.getModel().getLogContext().getAll().get(k).getDetails());
      testCaseID = extentTest.getModel().getLogContext().getAll().get(k).getDetails().split(":")[0];
      testCaseIDStatusMap.put(testCaseID.trim(), status);
    }

    if (testCaseID.isEmpty())

    {
      System.out.println("Test case ID details is not present in the logs");
      logger.warn("Test case ID details is not present in the logs");
    }

  }

  @SuppressWarnings("rawtypes")
  void logNestedException(Level level, Object msg, Throwable t) {
    if (t == null)
      return;
    try {
      Class tC = t.getClass();
      Method mA[] = tC.getMethods();
      Method nextThrowableMethod = null;

      for (int i = 0; i < mA.length; i++) {
        if ("getCause".equals(mA[i].getName()) || "getRootCause".equals(mA[i].getName())
            || "getNestedException".equals(mA[i].getName()) || "getException".equals(mA[i].getName())) {

          // Check param types
          Class params[] = mA[i].getParameterTypes();
          if (params == null || params.length == 0) {
            nextThrowableMethod = mA[i];
            break;
          }

        }
      }

      if (nextThrowableMethod != null) {
        // get the nested throwable and log it
        Throwable next = (Throwable) nextThrowableMethod.invoke(t, new Object[0]);
        if (next != null) {
          this.logger.log(FQCN, level, "Prevoius log CONTINUED", next);
        }
      }
    } catch (Exception e) {

    }
  }

}
