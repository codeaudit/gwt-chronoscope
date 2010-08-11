package org.timepedia.chronoscope.client.util.date;

import com.google.gwt.i18n.client.DateTimeFormat;

import org.timepedia.chronoscope.client.util.DateFormatter;

import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: ray Date: Oct 24, 2008 Time: 12:41:00 AM To
* change this template use File | Settings | File Templates.
*/
public class GWTDateFormatter implements DateFormatter {

  private DateTimeFormat fmt;

  public GWTDateFormatter(String format) {
    fmt = DateTimeFormat.getFormat(format);
  }

  public String format(double timestamp) {
    return fmt.format(new Date((long) timestamp));
  }

  public double parse(String date) {
    return fmt.parse(date).getTime();
  }
}
