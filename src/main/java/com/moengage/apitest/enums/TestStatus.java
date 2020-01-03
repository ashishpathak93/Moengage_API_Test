package com.moengage.apitest.enums;

import org.testng.ITestResult;

import com.moengage.apitest.base.BaseClass;

public enum TestStatus {

  FAIL(2) {

    @Override
    public int testStatus(ITestResult result) {
      BaseClass.getLogger().info("Test Failed with : {} ", result.getThrowable());
      return getTestResult();
    }

  },

  SKIP(3) {

    @Override
    public int testStatus(ITestResult result) {
      BaseClass.getLogger().info("Test Skipped with : {} ", result.getThrowable());
      return getTestResult();
    }

  },

  PASS(1) {

    @Override
    public int testStatus(ITestResult result) {
      BaseClass.getLogger().info("Testcases Passed");
      return getTestResult();
    }
  };

  private final int result;

  TestStatus(int result) {
    this.result = result;
  }

  public int getTestResult() {
    return result;
  }

  public abstract int testStatus(ITestResult result);

  public static TestStatus exchangeValueToEnum(int status) throws Exception {
    for (TestStatus testStatus : TestStatus.values()) {
      if (testStatus.getTestResult() == status) {
        return testStatus;
      }
    }
    throw new Exception("Unable to get the testcase status");
  }

}
