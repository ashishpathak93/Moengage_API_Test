package com.moengage.apitest.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import com.moengage.apitest.pojo.DataObject;

public class TCsEntity {

  @DataProvider(name = "dp")
  public Object[] getData(ITestContext context, ITestNGMethod method) {

    Map<String, String> hmap = context.getCurrentXmlTest().getAllParameters();
    List<Object> dataSet = new ArrayList<Object>();
    DataObject object = new DataObject();
    object.setTcId(context.getCurrentXmlTest().getName());
    object.setDescription(hmap.get("Test Description").toString());
    object.setApiName(hmap.get("Api Name").toString());
    object.setApiType(hmap.get("Api Type").toString());
    object.setApiUri(hmap.get("Uri").toString());
    object.setHeader(hmap.get("Header Component").toString());
    object.setBody(hmap.get("Params").toString());
    object.setExpectedOutput(hmap.get("Expected Output").toString());
    object.setSkippedData(hmap.get("SkippedData").toString());
    object.setDependentTest(hmap.get("Dependency").toString());
    dataSet.add(object);
    DataObject[] data = dataSet.toArray(new DataObject[0]);
    return data;
  }
}