package com.moengage.apitest.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;

import com.moengage.apitest.config.GlobalVars;

public class CommonUtils {
  LocalDate START_DATE;
  LocalDate END_DATE;

  /**
   * Get Random String
   * 
   * @author ashishpathak
   * @param count
   * @return String
   */
  public String getAlphaNumericString(int count) {
    String generatedString = RandomStringUtils.random(count, true, true);
    return generatedString;
  }

  /**
   * Get random number in integer
   * 
   * @author ashishpathak
   * @return int
   */

  public int getRandomNumberInt() {
    Random r = new Random(System.currentTimeMillis());
    return 10000 + r.nextInt(20000);
  }

  /**
   * Get random number in double
   * 
   * @return Double
   */
  public Double getRandomNumberDouble() {
    Random r = new Random(System.currentTimeMillis());
    Double d = (double) (10000 + r.nextInt(20000));
    return d;
  }

  /**
   * Get the current date in UTC
   * 
   * @author ashishpathak
   * @param format
   * @return String
   */
  public String getCurrentDate(String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(new Date());
  }

  /**
   * Get the default map with random values.
   * 
   * @author ashishpathak
   * @return HashMap<String,Object>
   */
  public HashMap<String, Object> generateMap() {
    createRandomEndDate(2021, 2050);
    HashMap<String, Object> hmap = new HashMap<String, Object>();

    hmap.put("$StartDate", START_DATE.toString());
    hmap.put("$StartDateTime", START_DATE.toString() + "T07:29:45.955Z");
    hmap.put("$EndDate", END_DATE.toString());
    hmap.put("$Date", getCurrentDate("yyyy-MM-dd"));
    hmap.put("$String", getAlphaNumericString(5));
    hmap.put("$Number", getRandomNumberInt());
    hmap.put("$Double", getRandomNumberDouble());
    hmap.put("$hash", GenerateMD5Hash.getMd5("1" + GlobalVars.PRIVATEKEY + GlobalVars.PUBLICKEY));
    hmap.put("$apikey", GlobalVars.PUBLICKEY);
    return hmap;
  }

  public int createRandomIntBetween(int start, int end) {
    return start + (int) Math.round(Math.random() * (end - start));
  }

  public LocalDate createRandomEndDate(int startYear, int endYear) {
    int day = createRandomIntBetween(1, 28);
    int month = createRandomIntBetween(1, 11);
    int year = createRandomIntBetween(startYear, endYear);
    START_DATE = LocalDate.of(year, month, day);
    END_DATE = LocalDate.of(year, month + 1, day);
    return LocalDate.of(year, month, day);
  }
}
