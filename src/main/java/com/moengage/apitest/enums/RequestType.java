package com.moengage.apitest.enums;

import java.util.Map;

import org.json.JSONObject;

import com.moengage.apitest.utils.JsonReader;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public enum RequestType {

  GET("get") {

    @Override
    public Response request(RequestSpecification request, String path, JSONObject requestParams) {
      Map<String, Object> data = new JsonReader().jsonParser(requestParams);
      for (Map.Entry<String, Object> entrys : data.entrySet()) {
        String id = entrys.getKey();
        String value = entrys.getValue().toString();
        request = request.param(id, value);
      }
      System.out.println("*********************" + path);
      return request.get(path);
    }
  },

  POST("post") {

    @Override
    public Response request(RequestSpecification request, String path, JSONObject requestParams) {
      request.body(requestParams.toString());
      return request.post(path);
    }

  },
  PUT("put") {

    @Override
    public Response request(RequestSpecification request, String path, JSONObject requestParams) {
      request.body(requestParams.toString());
      return request.put(path);
    }

  },
  DELETE("delete") {

    @Override
    public Response request(RequestSpecification request, String path, JSONObject requestParams) {
      Map<String, Object> data = new JsonReader().jsonParser(requestParams);
      for (Map.Entry<String, Object> entrys : data.entrySet()) {
        String id = entrys.getKey();
        String value = entrys.getValue().toString();
        request.param(id, value);
      }
      return request.delete(path);
    }

  };

  private final String type;

  RequestType(String type) {
    this.type = type;
  }

  public String getRequestType() {
    return type;
  }

  public abstract Response request(RequestSpecification request, String path, JSONObject requestParams);

}
