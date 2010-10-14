package org.timepedia.chronoscope.java2d;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by ray
 */

public class JDKDateFormatter implements DateFormatter {
    private static Date d = new Date();
    private SimpleDateFormat fmt;

    public JDKDateFormatter(String format) {
        fmt = new SimpleDateFormat(format);
    }

    public String format(double timestamp) {
      d.setTime((long) timestamp);
      return fmt.format(d);
    }

    public String format(double timestamp, TimeZone timezone) {
      d.setTime((long)timestamp);
      return fmt.format(d); //FIXME
      // FIXME return fmt.format(new Date((long) timestamp), timeZoneOffset);
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
