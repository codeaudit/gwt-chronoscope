package org.timepedia.chronoscope.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Utility for parsing extended date formats
 */
public class DateParser {
  private static Date date = new Date(0);

  public static double parse(String fmt, String dateString) {
    date.setTime(0);
    if(fmt == null) return Date.parse(dateString);
    DateTimeFormat dtf = DateTimeFormat.getFormat(fmt);
    dtf.parse(dateString, 0, date);
    return date.getTime();
  }
}
