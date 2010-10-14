package org.timepedia.chronoscope.client.util.date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.i18n.client.TimeZone;
import org.timepedia.chronoscope.client.util.DateFormatter;

import java.util.Date;

public class GWTDateFormatter implements DateFormatter {
  // re-using a scratch Date in order to avoid new Date()
  private static Date d = new Date();

  private DateTimeFormat fmt;

  public GWTDateFormatter(String format) {
    fmt = DateTimeFormat.getFormat(format);
  }

  public String format(double timestamp) {
    d.setTime((long) timestamp);
    return fmt.format(d);
  }

  public String format(double timestamp, TimeZone timeZone) {
    d.setTime((long)timestamp);
    return fmt.format(d, timeZone);
  }

  public double parse(String date) {
    d.setMonth(0);
    d.setDate(1);
    d.setHours(0);
    d.setMinutes(0);
    d.setSeconds(0);
    d.setTime((long)(d.getTime()/1000)*1000);
    fmt.parse(date, 0, d);
    return d.getTime();
  }

}
