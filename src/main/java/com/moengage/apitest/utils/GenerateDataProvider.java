package com.moengage.apitest.utils;

import java.util.List;

import org.testng.annotations.Test;

import com.moengage.apitest.base.BaseClass;

public class GenerateDataProvider {
  @Test
  public List<List<Object>> testDataSet(String sheetName) throws Exception {
    List<List<Object>> values = null;
    try {
      values = GoogleSheetIntegration.readTable(sheetName);
      return values;
    } catch (Exception e) {
      BaseClass.getLogger().fail("Error while reading excel sheet");
    }
    throw new Exception("Error while reading sheet");
  }
}
