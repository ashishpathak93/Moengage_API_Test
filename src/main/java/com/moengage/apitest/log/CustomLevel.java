package com.moengage.apitest.log;

import org.apache.log4j.Level;

public final class CustomLevel extends Level {

  /**
  	 * 
  	 */
  private static final long serialVersionUID = 1L;
  static public final int TRACE_INT = Level.DEBUG_INT - 1;
  private static String TRACE_STR = "TRACE";

  public static final CustomLevel TRACE = new CustomLevel(TRACE_INT, TRACE_STR, 7);

  protected CustomLevel(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);

  }

  public static Level toLevel(String sArg) {
    return toLevel(sArg, CustomLevel.TRACE);
  }

  public static Level toLevel(String sArg, Level defaultLevel) {

    if (sArg == null) {
      return defaultLevel;
    }
    String stringVal = sArg.toUpperCase();
    if (stringVal.equals(TRACE_STR)) {
      return CustomLevel.TRACE;
    }
    return Level.toLevel(sArg, defaultLevel);
  }

  public static Level toLeval(int i) {

    return toLevel(i, Level.DEBUG);
  }

}
