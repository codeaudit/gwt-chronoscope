package org.timepedia.chronoscope.client.data;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Utility for parsing extended date formats
 */
public class DateParser {
  public static double parse(String fmt, String dateString) {
    if(fmt == null) return Date.parse(dateString);
    DateTimeFormat dtf = DateTimeFormat.getFormat(fmt);
    Date date = new Date(70, 0, 1, 0, 0 ,0); 
    dtf.parse(dateString, 0, date);
    return date.getTime();
  }
}
