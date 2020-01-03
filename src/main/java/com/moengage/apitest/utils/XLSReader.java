package com.moengage.apitest.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.moengage.apitest.config.GlobalVars;
import com.moengage.apitest.utils.GenerateDataProvider;
import com.moengage.apitest.utils.GoogleSheetIntegration;
import com.moengage.apitest.utils.JsonReader;

public class XLSReader {

  static JsonReader reader = new JsonReader();

  /**
   * Get the testcases dependency List in sequential way
   * 
   * @author ashishpathak
   * @return LinkedList<String>
   * @throws Exception
   */
  public Set<String> getDependencyList() throws Exception {

    // Get the data from the excel sheet into a Map
    TreeMap<String, TreeMap<String, String>> dataFromExcel = getTests();

    // Initializing map and Graph to get dependency list
    TreeMap<String, String> testcaseVal = new TreeMap<String, String>();
    HashMap<Integer, String> testcaseID = new HashMap<Integer, String>();
    MutableGraph<String> graph = GraphBuilder.undirected().nodeOrder(ElementOrder.insertion()).build();
    int i = 0;

    // Get the Testcases values in Treemap and Get the Testcases List in HashMap
    // with it's order
    for (Map.Entry<String, TreeMap<String, String>> entry : dataFromExcel.entrySet()) {
      testcaseVal = entry.getValue();
      graph.addNode(entry.getKey());

      // testcaseID.put(entry.getKey(), i);
      testcaseID.put(i, entry.getKey());
      i++;
    }

    // Add the testcases dependency relationship
    for (Entry<Integer, String> entry : testcaseID.entrySet()) {
      int key = entry.getKey();
      String value = entry.getValue();
      testcaseVal = dataFromExcel.get(value);
      LinkedList<String> list = reader.getDependencyList(testcaseVal.get("Dependency"));
      graph = addDependency(key, value, graph, list);
    }

    // Reverse topological sorting
    Set<String> testcaseId = graph.nodes();
    return testcaseId;
  }

  /**
   * Get the testcases list from excel to map
   * 
   * @author ashishpathak
   * @return TreeMap<String,TreeMap<String,String>>
   * @throws Exception
   */
  public TreeMap<String, TreeMap<String, String>> getTests() throws Exception {

    // Initialization of data structure to store excel data
    List<List<Object>> dataFromSheet = new ArrayList<List<Object>>();
    List<String> sheetList = new ArrayList<String>();

    // Use to set the Pojo
    GenerateDataProvider dataProvider = new GenerateDataProvider();

    // Getting the list of sheets
    try {
      // Read all values from the excel sheet
      if (GlobalVars.SHEETNAME.length() == 0) {
        sheetList = GoogleSheetIntegration.getSheetName();
      } else
        sheetList.addAll(Arrays.asList(GlobalVars.SHEETNAME.split(",")));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Storing all sheets data based on it's dependency
    TreeMap<String, TreeMap<String, String>> data = new TreeMap<String, TreeMap<String, String>>();
    for (int i = 0; i < sheetList.size(); i++) {
      dataFromSheet = dataProvider.testDataSet(sheetList.get(i));
      for (int j = 1; j < dataFromSheet.size(); j++) {

        String key = dataFromSheet.get(j).get(0).toString();
        TreeMap<String, String> subset = new TreeMap<String, String>();
        int columnSize = dataFromSheet.get(j).size();

        for (int k = 1; k < columnSize; k++) {
          subset.put(GlobalVars.TC_HEADERS.get(k), dataFromSheet.get(j).get(k).toString());
        }
        data.put(key, subset);
      }
    }
    return data;
  }

  /**
   * Add dependency list for each testcases(DAG)
   * 
   * @author ashishpathak
   * @param keys
   * @param g
   * @param list
   * @param hmap
   * @return Graph
   */
  public static MutableGraph<String> addDependency(int key, String value, MutableGraph<String> g,
      LinkedList<String> list) {
    if (list.isEmpty()) {
      return g;
    } else {
      for (int i = 0; i < list.size(); i++) {
        g.putEdge(value, list.get(i));
      }
    }
    return g;
  }
}
