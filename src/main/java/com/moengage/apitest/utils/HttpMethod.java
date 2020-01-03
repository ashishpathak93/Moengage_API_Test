package com.moengage.apitest.utils;

import java.io.FileNotFoundException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.moengage.apitest.base.BaseClass;
import com.moengage.apitest.config.GlobalVars;
import com.moengage.apitest.enums.RequestType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HttpMethod {
  static Response response;
  static JSONObject requestParams;
  static String value;

  /**
   * Get the response from any http request type
   * 
   * @author ashishpathak
   * @param apiType
   * @param header
   * @param params
   * @param path
   * @param dependency
   * @return Response
   * @throws Exception
   */
  public static Response getRequestInfo(String testcaseId, String apiType, String header, String params, String path,
      String dependency, JsonReader reader) throws Exception {

    String baseURI = GlobalVars.ENVIRONMENT;

    System.out.println(baseURI);
    RestAssured.baseURI = baseURI;

    RequestSpecification request = RestAssured.given();
    // check whether header is empty or not
    if (!header.isEmpty()) {
      String[] headers = header.split(GlobalVars.NEXTLINE);
      for (int i = 0; i < headers.length; i++) {
        String[] head = headers[i].split(GlobalVars.COLON);
        String key = head[0];
        String value = head[1];
        request.header(key, value);
      }
    }

    // Add the Json to the body of the request
    if (!params.isEmpty()) {
      try {
        requestParams = getJsonPayload(testcaseId, params, dependency, reader);
        BaseClass.getLogger().info("Payload data - " + requestParams.toString());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    System.out.println(requestParams);
    try {
      response = RequestType.valueOf(apiType.toUpperCase()).request(request, path, requestParams);
      return response;
    } catch (Exception e) {
      BaseClass.getLogger().fail(ExceptionUtils.getStackTrace(e));
      throw new Exception(ExceptionUtils.getStackTrace(e));
    }
  }

  public static JSONObject getJsonPayload(String testcaseId, String params, String dependency, JsonReader reader)
      throws FileNotFoundException {
    return reader.getResourceJsonData(testcaseId, params, dependency);
  }

}
