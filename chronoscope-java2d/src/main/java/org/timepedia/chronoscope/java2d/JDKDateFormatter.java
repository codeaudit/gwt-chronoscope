package org.timepedia.chronoscope.java2d;

import org.timepedia.chronoscope.client.util.DateFormatter;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: ray Date: Oct 24, 2008 Time: 12:41:53 AM To
* change this template use File | Settings | File Templates.
*/
public class JDKDateFormatter implements DateFormatter {

  private SimpleDateFormat fmt;

  public JDKDateFormatter(String format) {
    fmt = new SimpleDateFormat(format);
  }

  public String format(double timestamp) {
    return fmt.format(new Date((long) timestamp));
  }

  public double parse(String date) {
    try {
      return fmt.parse(date).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
      return 0;
    }
  }
}
