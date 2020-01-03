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
import com.moengage.apitest.config.GlobalVars;
import com.moengage.apitest.pojo.DataObject;
import com.moengage.apitest.report.ExtentTestManager;
import com.moengage.apitest.testdata.TCsEntity;
import com.moengage.apitest.utils.HttpMethod;
import com.moengage.apitest.utils.JsonReader;

import io.restassured.response.Response;

public class CharacterAPITest extends BaseClass {

  private Response response;
  JsonReader reader = new JsonReader();

  @Test(dataProvider = "dp", dataProviderClass = TCsEntity.class)
  public void getCharactersDetails(DataObject data) throws Exception {
    ExtentTestManager.startTest(data.getTcId() + "_1", data.getDescription());
    BaseClass.getLogger().info("Starting Testcase id - " + data.getTcId());
    BaseClass.getLogger().info("Api Description - " + data.getDescription());
    String[] uri = data.getApiUri().split("\n");

    BaseClass.getLogger().info(" Url to Test - " + uri[0] + " of " + data.getApiType() + " Http Request Type");

    response = HttpMethod.getRequestInfo(data.getTcId() + "_1", data.getApiType(), data.getHeader(), data.getBody(),
        uri[0], data.getDependentTest(), reader);

    BaseClass.getLogger().info("Response is - " + response.getBody().asString() + "\n");
    BaseClass.getLogger().info("Status code - " + response.getStatusCode());

    if (response.statusCode() == 200) {
      // List of character id
      List<Integer> list = reader.getListOfCharacterID(response, "id");

      // Map of characterId and name where description is not empty
      HashMap<Integer, String> hmap = getListOfCharacterWithDesc(list, data);
      BaseClass.getLogger().info("Chacter name : " + hmap);
      GlobalVars.RESPONSEMAP.put(data.getTcId() + "_1", reader.getCharAsString(hmap));

      // Map of characterId and series
      HashMap<Integer, HashMap<Integer, String>> series = getListOfSeries(hmap, data);
      BaseClass.getLogger().info("Series details : " + series);

      // Randomly Selects 2 series
      String character = reader.getListOfCharId(series, 2);
      GlobalVars.RESPONSEMAP.put(data.getTcId() + "_2", character);
      // getListOfCharName(character, data);
    } else {
      BaseClass.getLogger().fail("Failure: " + response.statusCode() + " Message: " + response);
    }
  }

  @Test(dependsOnMethods = { "getCharactersDetails" }, dataProvider = "dp", dataProviderClass = TCsEntity.class)
  public void getSeries(DataObject data) throws Exception {
    HashMap<Integer, List<String>> hmap = new HashMap<Integer, List<String>>();
    String[] uri = data.getApiUri().split("\n");
    ExtentTestManager.startTest(data.getTcId() + "_2", data.getDescription());
    BaseClass.getLogger().info("Starting Testcase id - " + data.getTcId());
    BaseClass.getLogger().info("Api Description - " + data.getDescription());
    BaseClass.getLogger().info(" Url to Test - " + uri[1] + " of " + data.getApiType() + " Http Request Type");

    String[] seriesId = GlobalVars.RESPONSEMAP.get(data.getTcId() + "_2").split(",");
    for (int i = 0; i < seriesId.length; i++) {
      response = HttpMethod.getRequestInfo(data.getTcId() + "_2", data.getApiType(), data.getHeader(), data.getBody(),
          uri[1] + "/" + seriesId[i] + "/characters", data.getDependentTest(), reader);
      if (response.statusCode() == 200) {
        JsonParser jsonParser = new JsonParser();
        JsonArray datas = jsonParser.parse(response.asString()).getAsJsonObject().get("data").getAsJsonObject()
            .getAsJsonArray("results");
        List<String> list = new LinkedList<String>();
        for (JsonElement result : datas) {
          list.add(result.getAsJsonObject().get("name").getAsString());
        }
        hmap.put(Integer.parseInt(seriesId[i]), list);
      } else
        BaseClass.getLogger().warn("Failure: " + response.statusCode() + " Message: " + response);
    }

    BaseClass.getLogger().info("Response is - " + response.getBody().asString() + "\n");
    BaseClass.getLogger().info("Status code - " + response.getStatusCode());
    BaseClass.getLogger().info("Series with character id: " + hmap);

  }

  public HashMap<Integer, String> getListOfCharacterWithDesc(List<Integer> list, DataObject data) throws Exception {
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
          if (!result.getAsJsonObject().get("description").getAsString().isEmpty()) {
            hmap.put((result.getAsJsonObject().get("id").getAsInt()),
                result.getAsJsonObject().get("name").getAsString());
          }
        }
      } else
        BaseClass.getLogger().warn("Failure: " + response.statusCode() + " Message: " + response);
    }
    return hmap;
  }

  public HashMap<Integer, HashMap<Integer, String>> getListOfSeries(HashMap<Integer, String> hmap, DataObject data)
      throws Exception {
    String[] uri = data.getApiUri().split("\n");
    HashMap<Integer, HashMap<Integer, String>> series = new HashMap<Integer, HashMap<Integer, String>>();
    for (Map.Entry<Integer, String> map : hmap.entrySet()) {
      response = HttpMethod.getRequestInfo(data.getTcId(), data.getApiType(), data.getHeader(), data.getBody(),
          uri[0] + "/" + map.getKey() + "/series", data.getDependentTest(), reader);

      if (response.statusCode() == 200) {
        JsonParser jsonParser = new JsonParser();
        JsonArray datas = jsonParser.parse(response.asString()).getAsJsonObject().get("data").getAsJsonObject()
            .getAsJsonArray("results");
        HashMap<Integer, String> seriesData = new HashMap<Integer, String>();
        for (JsonElement result : datas) {
          seriesData.put(result.getAsJsonObject().get("id").getAsInt(),
              result.getAsJsonObject().get("title").getAsString());
        }
        series.put((map.getKey().intValue()), seriesData);
      } else
        BaseClass.getLogger().warn("Failure: " + response.statusCode() + " Message: " + response);
    }
    return series;
  }
}
