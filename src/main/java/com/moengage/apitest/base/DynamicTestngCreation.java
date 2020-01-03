package com.moengage.apitest.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.moengage.apitest.config.GlobalVars;
import com.moengage.apitest.utils.JsonReader;
import com.moengage.apitest.utils.XLSReader;

public class DynamicTestngCreation {

  JsonReader reader = new JsonReader();
  XLSReader xlsReader = new XLSReader();

  /**
   * Generate and execute dynamic testng.xml on fly
   * 
   * @author ashishpathak
   * @throws Exception
   */
  public void runTestNGTest() throws Exception {

    // Initializing treemap to get the data
    TreeMap<String, String> data = new TreeMap<String, String>();

    // Initializing testng
    TestNG myTestNG = new TestNG();
    XmlSuite mySuite = new XmlSuite();
    mySuite.setName(GlobalVars.REPORTNAME);

    // Reading data from Excel sheet
    TreeMap<String, TreeMap<String, String>> testcases = xlsReader.getTests();

    // Read and get the testcasesid
    Set<String> testcaseId = xlsReader.getDependencyList();

    Iterator<String> iterator = testcaseId.iterator();

    while (iterator.hasNext()) {
      String currentTest = iterator.next();
      LinkedList<String> list = reader.getDependencyList(testcases.get(currentTest).get("Dependency"));

      if (!list.isEmpty()) {
        for (int i = 0; i < list.size(); i++) {
          List<XmlClass> myClasses = new ArrayList<XmlClass>();
          myClasses.add(new XmlClass(testcases.get(currentTest).get("ClassName")));
          XmlTest myTest = new XmlTest(mySuite);
          myTest.setName(currentTest + "_" + list.get(i));
          System.out.println(currentTest + "_" + list.get(i));
          myTest.setPreserveOrder(true);

          data = testcases.get(list.get(i));
          myTest.setParameters(data);
          myTest.setClasses(myClasses);
        }
      }

      List<XmlClass> myClasses = new ArrayList<XmlClass>();
      myClasses.add(new XmlClass(testcases.get(currentTest).get("ClassName")));

      XmlTest myTest = new XmlTest(mySuite);
      myTest.setName(currentTest);
      System.out.println(currentTest);
      myTest.setPreserveOrder(true);

      data = testcases.get(currentTest);
      myTest.setParameters(data);
      myTest.setClasses(myClasses);
    }

    // Prepare TestNg.xml from the excel
    List<XmlSuite> mySuites = new ArrayList<XmlSuite>();
    mySuites.add(mySuite);
    myTestNG.setXmlSuites(mySuites);
    TestListenerAdapter tla = new TestListenerAdapter();
    myTestNG.addListener(tla);

    // Execute Dynamic TestNG.xml on fly
    myTestNG.run();
  }

  // Calling the main method to run testNG method
  public static void main(String args[]) throws Exception {
    DynamicTestngCreation dt = new DynamicTestngCreation();
    dt.runTestNGTest();
  }
}
