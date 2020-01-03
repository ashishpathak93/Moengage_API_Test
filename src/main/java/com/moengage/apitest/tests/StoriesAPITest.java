package com.moengage.apitest.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.moengage.apitest.base.BaseClass;
import com.moengage.apitest.pojo.DataObject;
import com.moengage.apitest.report.ExtentTestManager;
import com.moengage.apitest.testdata.TCsEntity;
import com.moengage.apitest.utils.HttpMethod;
import com.moengage.apitest.utils.JsonReader;

import io.restassured.response.Response;

public class StoriesAPITest extends BaseClass {
  private Response response;
  JsonReader reader = new JsonReader();

  @Test(dataProvider = "dp", dataProviderClass = TCsEntity.class)
  public void testAPI(DataObject data) throws Exception {
    HashMap<Integer, List<String>> storiesMap = new HashMap<Integer, List<String>>();
    ExtentTestManager.startTest(data.getTcId(), data.getDescription());
    BaseClass.getLogger().info("Starting Testcase id - " + data.getTcId());
    BaseClass.getLogger().info("Api Description - " + data.getDescription());
    BaseClass.getLogger()
        .info(" Url to Test - " + data.getApiUri() + " of " + data.getApiType() + " Http Request Type");

    response = HttpMethod.getRequestInfo(data.getTcId(), data.getApiType(), data.getHeader(), data.getBody(),
        data.getApiUri(), data.getDependentTest(), reader);

    BaseClass.getLogger().info("Response is - " + response.getBody().asString() + "\n");
    BaseClass.getLogger().info("Status code - " + response.getStatusCode());

    if (response.statusCode() == 200) {
      // List of character id
      List<Integer> list = reader.getListOfCharacterID(response, "id");

      // Map of characterId and name where description not empty
      HashMap<Integer, String> charMap = getListOfCharacterWithoutDesc(list, data);

      System.out.println(charMap);
      // Get list of stories
      storiesMap = getStoriesList(charMap, data);

    }
    BaseClass.getLogger().info("Response is - " + response.getBody().asString() + "\n");
    BaseClass.getLogger().info("Status code - " + response.getStatusCode());
    BaseClass.getLogger().info("Stories with character id: " + storiesMap);
  }

  public HashMap<Integer, String> getListOfCharacterWithoutDesc(List<Integer> list, DataObject data) throws Exception {
    HashMap<Integer, String> hmap = new HashMap<Integer, String>();
    String[] uri = data.getApiUri().split("\n");
    for (int li : list) {
      response = HttpMethod.getRequestInfo(data.getTcId(), data.getApiType(), data.getHeader(), data.getBody(),
          uri[0] + "/" + li, data.getDependentTest(), reader);
      if (response.statusCode() == 200) {
        JsonParser jsonParser = new JsonParser();
        JsonArray datas = jsonParser.parse(response.asString()).getAsJsonObject().get("data").getAsJsonObject()
            .getAsJsonArray("results");

        for (JsonElement result : datas) {
          if (result.getAsJsonObject().get("description").getAsString().isEmpty()) {
            hmap.put((result.getAsJsonObject().get("id").getAsInt()),
                result.getAsJsonObject().get("name").getAsString());
          }
        }
      } else
        BaseClass.getLogger().warn("Failure: " + response.statusCode() + " Message: " + response);
    }
    return hmap;
  }

  public HashMap<Integer, List<String>> getStoriesList(HashMap<Integer, String> hmap, DataObject data)
      throws Exception {
    HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();

    for (Map.Entry<Integer, String> maps : hmap.entrySet()) {
      response = HttpMethod.getRequestInfo(data.getTcId(), data.getApiType(), data.getHeader(), data.getBody(),
          data.getApiUri() + "/" + maps.getKey() + "/stories", data.getDependentTest(), reader);

      List<String> list = new LinkedList();
      if (response.statusCode() == 200) {
        JsonParser jsonParser = new JsonParser();
        JsonArray datas = jsonParser.parse(response.asString()).getAsJsonObject().get("data").getAsJsonObject()
            .getAsJsonArray("results");

        for (JsonElement result : datas) {
          list.add(result.getAsJsonObject().get("title").getAsString());
        }
      } else
        BaseClass.getLogger().warn("Failure: " + response.statusCode() + " Message: " + response);

      map.put(maps.getKey(), list);
    }
    return map;
  }
}
