package com.moengage.apitest.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.moengage.apitest.base.BaseClass;
import com.moengage.apitest.config.GlobalVars;

import io.restassured.response.Response;

/**
 * @author ashishpathak
 *
 */
public class JsonReader {

  static CommonUtils utils = new CommonUtils();

  final static HashMap<String, Object> dynamicData = utils.generateMap();

  /**
   * Convert jsonobject to hashmap
   * 
   * @author ashishpathak
   * @param json
   * @return
   * @throws JSONException Map<String,Object>
   */
  public Map<String, Object> jsonParser(JSONObject json) throws JSONException {
    Map<String, Object> dataMap = new HashMap<String, Object>();
    if (json != JSONObject.NULL) {
      dataMap = toMap(json);
    }
    return dataMap;
  }

  /**
   * Get json key value from the jsonobject `
   * 
   * @author ashishpathak
   * @param json
   * @param name
   * @return
   * @throws JSONException String
   */
  public String jsonParser(JSONObject json, String name) throws JSONException {
    String value = toMap(json, name);
    return value;
  }

  /**
   * Get list of testcases id from the json string
   * 
   * @author ashishpathak
   * @param body
   * @return LinkedList<String>
   */
  public LinkedList<String> getDependencyList(String data) {
    JSONObject json = new JSONObject(data);
    Map<String, Object> t = jsonParser(json);
    LinkedList<String> list = new LinkedList<String>();

    for (Entry<String, Object> entry : t.entrySet()) {
      list.add(entry.getKey());
    }
    return list;
  }

  /**
   * Convert the Jsonobject to Hashmap
   * 
   * @author ashishpathak
   * @param object
   * @return
   * @throws JSONException Map<String,Object>
   */
  public Map<String, Object> toMap(JSONObject object) throws JSONException {
    Map<String, Object> map = new TreeMap<String, Object>();

    Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      }

      else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }

      else {
        if (dynamicData.containsKey(value))
          value = dynamicData.get(value);
      }
      map.put(key, value);
    }
    return map;
  }

  /**
   * Get the jsonarray to list
   * 
   * @author ashishpathak
   * @param array
   * @return
   * @throws JSONException List<Object>
   */
  public List<Object> toList(JSONArray array) throws JSONException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++) {
      Object value = array.get(i);
      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      }

      else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }

      if (!dynamicData.containsKey(value))
        list.add(value);
      else
        list.add(dynamicData.get(value));

    }
    return list;
  }

  /**
   * Get the value from the JsonObject
   * 
   * @author ashishpathak
   * @param object
   * @return
   * @throws JSONException Map<String,Object>
   */
  public String toMap(JSONObject object, String name) throws JSONException {
    Iterator<String> keysItr = object.keys();
    String val = null;
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray && val == null) {
        value = toListNode((JSONArray) value, name);
      }

      else if (value instanceof JSONObject && val == null) {
        value = toMap((JSONObject) value, name);
      }

      if (key.equalsIgnoreCase(name)) {
        val = value.toString();

      }
    }
    return val;
  }

  /**
   * Get the jsonarray to list
   * 
   * @author ashishpathak
   * @param array
   * @return
   * @throws JSONException List<Object>
   */
  public List<Object> toListNode(JSONArray array, String name) throws JSONException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++) {
      Object value = array.get(i);
      if (value instanceof JSONArray) {
        value = toListNode((JSONArray) value, name);
      }

      else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value, name);
      }

      if (!dynamicData.containsKey(value))
        list.add(value);
      else
        list.add(dynamicData.get(value));

    }
    return list;
  }

  /**
   * Delete any key-value from the JsonObject
   * 
   * @author ashishpathak
   * @param object
   * @param fieldNames
   * @return
   * @throws JSONException Map<String,Object>
   */
  public Map<String, Object> deleteMap(JSONObject object, List<String> fieldNames) throws JSONException {
    Map<String, Object> map = new TreeMap<String, Object>();

    Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray) {
        value = addToList((JSONArray) value, fieldNames);
      }

      else if (value instanceof JSONObject) {
        value = deleteMap((JSONObject) value, fieldNames);
      }

      if (fieldNames.contains(key))
        map.remove(key);
      else
        map.put(key, value);

    }
    return map;
  }

  public List<Object> addToList(JSONArray array, List<String> fieldNames) throws JSONException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++) {
      Object value = array.get(i);
      if (value instanceof JSONArray) {
        value = addToList((JSONArray) value, fieldNames);
      }

      else if (value instanceof JSONObject) {
        value = deleteMap((JSONObject) value, fieldNames);
      }

      if (!dynamicData.containsKey(value))
        list.add(value);
      else
        list.add(dynamicData.get(value));

    }
    return list;
  }

  /**
   * Get JsonObject from Json String
   * 
   * @param body
   * @return
   * @throws FileNotFoundException JSONObject
   */
  public JSONObject getResourceJsonData(String body) throws FileNotFoundException {
    JSONObject json = new JSONObject(body);
    Map<String, Object> map = jsonParser(json);
    JSONObject object = new JSONObject(map);
    return object;
  }

  /**
   * Update Jsonobject by modifying actual json i.e payload
   * 
   * @param body
   * @param dependentTest
   * @return
   * @throws FileNotFoundException JSONObject
   */
  public JSONObject getResourceJsonData(String testcaseId, String body, String dependentTest)
      throws FileNotFoundException {
    String regex = "\\d+";
    JSONObject jsonActual = new JSONObject(body);
    if (dependentTest.length() > 2) {
      Map<String, Object> actual = jsonParser(jsonActual);
      JSONObject jsonDependent = new JSONObject(dependentTest);
      Map<String, Object> dependent = jsonParser(jsonDependent);
      HashMap<String, String> response = GlobalVars.RESPONSEMAP;

      for (Map.Entry<String, Object> entry : dependent.entrySet()) {
        Map<String, Object> dependentValue = (Map<String, Object>) entry.getValue();
        String reponse = response.get(entry.getKey());
        if (!response.isEmpty()) {

          JSONObject jsonValue = new JSONObject(reponse);
          for (Map.Entry<String, Object> entrys : dependentValue.entrySet()) {
            String id = entrys.getKey();
            String value = jsonParser(jsonValue, id);
            Set<String> s = getKeysByValue(actual, entrys.getValue().toString());
            for (String aSiteId : s) {
              id = aSiteId;
              break;
            }

            try {
              if (value.matches(regex)) {
                jsonActual = updateJson(jsonActual, id, entrys.getValue().toString(), Integer.parseInt(value));
              } else {
                try {
                  double val = Double.parseDouble(value);
                  jsonActual = updateJson(jsonActual, id, entrys.getValue().toString(), val);
                } catch (NumberFormatException e) {
                  jsonActual = updateJson(jsonActual, id, entrys.getValue().toString(), value);
                }

              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        } else {
          BaseClass.getLogger()
              .skip("Skipping Test case" + testcaseId + " since dependent test are not executed successfully");
        }

      }
    } else {
      Map<String, Object> map = jsonParser(jsonActual);
      JSONObject object = new JSONObject(map);
      return object;
    }
    return jsonActual;
  }

  /**
   * Validate json by providing the below inputs
   * 
   * @author ashishpathak
   * @param response
   * @param expected
   * @param toBeSkipped
   * @param dependent
   * @return String
   */
  public String postValidation(String testCaseId, Response response, String expected, String toBeSkipped,
      String dependent) {

    JsonNode patchNode = null;
    try {
      List<String> fieldNames = Arrays.asList(toBeSkipped.split(","));
      ObjectMapper jackson = new ObjectMapper();

      // Trim few data from the Json => Json Response data
      JSONObject responseJson = new JSONObject(response.getBody().asString());
      Map<String, Object> map = deleteMap(responseJson, fieldNames);
      responseJson = new JSONObject(map);
      JsonNode beforeNode = toJsonNode(responseJson);

      // Modify data at runtime from dependent column
      JsonNode afterNode = jackson
          .readTree(new JsonReader().getResourceJsonData(testCaseId, expected, dependent).toString());
      BaseClass.getLogger().info("Expected Output is: " + afterNode.toString());

      // Trim few data from the Json => Expected Json data
      JSONObject expectedJson = new JSONObject(afterNode.toString());
      Map<String, Object> expectedMap = deleteMap(expectedJson, fieldNames);
      expectedJson = new JSONObject(expectedMap);
      afterNode = toJsonNode(expectedJson);

      // Find the Diff between two json Node
      patchNode = JsonDiff.asJson(beforeNode, afterNode);

      return patchNode.toString();
    } catch (Exception e) {
      return ExceptionUtils.getStackTrace(e);
    }
  }

  /**
   * Update any value of json object
   * 
   * @author ashishpathak
   * @param obj
   * @param keyMain
   * @param valueMain
   * @param newValue
   * @return
   * @throws Exception JSONObject
   */
  public JSONObject updateJson(JSONObject obj, String keyMain, String valueMain, String newValue) throws Exception {
    // We need to know keys of Jsonobject
    JSONObject json = new JSONObject();
    Iterator iterator = obj.keys();
    String key = null;
    while (iterator.hasNext()) {
      key = (String) iterator.next();
      // if object is just string we change value in key
      if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
        if ((key.equals(keyMain)) && (obj.get(key).toString().equals(valueMain))) {
          // put new value
          obj.put(key, newValue);
          return obj;
        }
      }

      // if it's jsonobject
      if (obj.optJSONObject(key) != null) {
        updateJson(obj.getJSONObject(key), keyMain, valueMain, newValue);
      }

      // if it's jsonarray
      if (obj.optJSONArray(key) != null) {
        JSONArray jArray = obj.getJSONArray(key);
        for (int i = 0; i < jArray.length(); i++) {
          updateJson(jArray.getJSONObject(i), keyMain, valueMain, newValue);
        }
      }
    }
    return obj;
  }

  /**
   * Update any value of json object in case of integer
   * 
   * @author ashishpathak
   * @param obj
   * @param keyMain
   * @param valueMain
   * @param newValue
   * @return
   * @throws Exception JSONObject
   */
  public JSONObject updateJson(JSONObject obj, String keyMain, String valueMain, int newValue) throws Exception {
    // We need to know keys of Jsonobject
    JSONObject json = new JSONObject();
    Iterator iterator = obj.keys();
    String key = null;
    while (iterator.hasNext()) {
      key = (String) iterator.next();
      // if object is just string we change value in key
      if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
        if ((key.equals(keyMain)) && (obj.get(key).toString().equals(valueMain))) {
          // put new value
          obj.put(key, newValue);
          return obj;
        }
      }

      // if it's jsonobject
      if (obj.optJSONObject(key) != null) {
        updateJson(obj.getJSONObject(key), keyMain, valueMain, newValue);
      }

      // if it's jsonarray
      if (obj.optJSONArray(key) != null) {
        JSONArray jArray = obj.getJSONArray(key);
        for (int i = 0; i < jArray.length(); i++) {
          updateJson(jArray.getJSONObject(i), keyMain, valueMain, newValue);
        }
      }
    }
    return obj;
  }

  /**
   * Update any value of json object in case of double
   * 
   * @author ashishpathak
   * @param obj
   * @param keyMain
   * @param valueMain
   * @param newValue
   * @return
   * @throws Exception JSONObject
   */
  public JSONObject updateJson(JSONObject obj, String keyMain, String valueMain, double newValue) throws Exception {
    // We need to know keys of Jsonobject
    JSONObject json = new JSONObject();
    Iterator iterator = obj.keys();
    String key = null;
    while (iterator.hasNext()) {
      key = (String) iterator.next();
      // if object is just string we change value in key
      if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
        if ((key.equals(keyMain)) && (obj.get(key).toString().equals(valueMain))) {
          // put new value
          obj.put(key, newValue);
          return obj;
        }
      }

      // if it's jsonobject
      if (obj.optJSONObject(key) != null) {
        updateJson(obj.getJSONObject(key), keyMain, valueMain, newValue);
      }

      // if it's jsonarray
      if (obj.optJSONArray(key) != null) {
        JSONArray jArray = obj.getJSONArray(key);
        for (int i = 0; i < jArray.length(); i++) {
          updateJson(jArray.getJSONObject(i), keyMain, valueMain, newValue);
        }
      }
    }
    return obj;
  }

  /**
   * Get the keys by searching value from hashmap
   * 
   * @author ashishpathak
   * @param <T>
   * @param <E>
   * @param map
   * @param value
   * @return Set<T>
   */
  public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
    return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  /**
   * Convert JsonObject to JsonNode
   * 
   * @author ashishpathak
   * @param jsonObj
   * @return
   * @throws IOException JsonNode
   */
  public static JsonNode toJsonNode(JSONObject jsonObj) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(jsonObj.toString());
  }

  public List<Integer> getListOfCharacterID(Response response, String name) throws IOException {
    JSONObject jsonActual = new JSONObject(response.asString()).getJSONObject("data");
    Map<String, Object> map = jsonParser(jsonActual);
    List<Integer> list = new ArrayList<Integer>();
    String key = map.get("results").toString();
    String[] str = key.split(",");
    for (int i = 0; i < str.length; i++) {
      if (str[i].contains((name + "="))) {
        list.add(Integer.parseInt((str[i].split("=")[1])));
      }
    }
    return list;
  }

  public String getListOfCharId(HashMap<Integer, HashMap<Integer, String>> hmap, int noOfSeries) {
    String list = "";
    for (int i = 0; i < noOfSeries; i++) {
      for (Map.Entry<Integer, HashMap<Integer, String>> map : hmap.entrySet()) {
        if (list.split(",").length == noOfSeries)
          break;

        HashMap<Integer, String> nMap = map.getValue();
        for (Map.Entry<Integer, String> nestedMap : nMap.entrySet()) {
          list = list + nestedMap.getKey() + ",";
        }
      }
    }
    System.out.println(list);
    return list;
  }

  public String getCharAsString(HashMap<Integer, String> hmap) {
    String charId = "";
    for (Map.Entry<Integer, String> map : hmap.entrySet()) {
      charId += map.getKey() + ",";
    }
    return charId;
  }
}
