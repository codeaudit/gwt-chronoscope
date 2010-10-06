package org.timepedia.chronoscope.client.util.date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.i18n.client.TimeZone;
import org.timepedia.chronoscope.client.util.DateFormatter;

import java.util.Date;

public class GWTDateFormatter implements DateFormatter {

  private DateTimeFormat fmt;

  public GWTDateFormatter(String format) {
    fmt = DateTimeFormat.getFormat(format);
  }

  public String format(double timestamp) {
    return fmt.format(new Date((long) timestamp));
  }

  public String format(double timestamp, TimeZone timeZone) {
    return fmt.format(new Date((long) timestamp), timeZone);
  }

  public double parse(String date) {
    Date d = new Date();
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
