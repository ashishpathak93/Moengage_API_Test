package com.moengage.apitest.report;

import java.util.HashMap;
import java.util.Map;

import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class ExtentTestManager implements ITestListener, IConfigurationListener {

  private static ExtentReports extent = ExtentManager.getInstance();
  private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal();
  private static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
  static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
  private static Map<Integer, String> testDetailMap = new HashMap<Integer, String>();
  private static ExtentTest extentTest;

  @Override
  public void onConfigurationFailure(ITestResult iTestResult) {
  }

  @Override
  public void onConfigurationSkip(ITestResult iTestResult) {
    String testName = iTestResult.getName();
    ExtentTest child = parentTest.get().createNode(testName);
    test.set(child);
    test.get().skip("Skipped");

  }

  public static Map<Integer, String> getTestDetailMap() {
    return testDetailMap;
  }

  public static Map<Integer, ExtentTest> getExtentTestMap() {
    return extentTestMap;
  }

  @Override
  public void onFinish(ITestContext context) {
    extent.flush();
  }

  @Override
  public void onStart(ITestContext context) {
    ExtentTest parent = extent.createTest(context.getName());
    parentTest.set(parent);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    ExtentTest extentTest = extent.createTest(result.getTestContext().getAttribute("TestName").toString(),
        result.getTestContext().getAttribute("TestDescription").toString());
    test.set(extentTest);
    test.get().fail(result.getThrowable());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    ExtentTest extentTest = extent.createTest(result.getTestContext().getAttribute("TestName").toString(),
        result.getTestContext().getAttribute("TestDescription").toString());
    test.set(extentTest);
    test.get().pass("Test passed");
  }

  public static synchronized ExtentTest startTest(String testName) {
    return startTest(testName, "");
  }

  public static synchronized ExtentTest startTest(String testName, String desc) {
    Integer threadId = (int) (Thread.currentThread().getId());
    ExtentTest test = extent.createTest(testName, desc);
    extentTestMap.put(threadId, test);
    testDetailMap.put(threadId, testName);
    return test;
  }

  public static synchronized ExtentTest startTest(String testName, String testDescription, String category) {
    Integer threadId = (int) (Thread.currentThread().getId());
    ExtentTest test = extent.createTest(testName, testDescription).assignCategory(category);
    extentTestMap.put(threadId, test);
    testDetailMap.put(threadId, testName);
    return test;
  }

  public static synchronized ExtentTest getTest() {
    return extentTestMap.get((Thread.currentThread().getId()));
  }

  public static void writeLog(String description) {

    extentTest.log(Status.INFO, description);
  }

  @Override
  public void onConfigurationSuccess(ITestResult itr) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onTestStart(ITestResult result) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onTestSkipped(ITestResult result) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    // TODO Auto-generated method stub

  }

}
