package org.timepedia.chronoscope.client.io;

import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;
import org.timepedia.chronoscope.client.data.json.JsonArrayString;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;

import java.util.Date;

/**
 * Parses JsArray objects into a primitive java <tt>double[]</tt> array.
 *
 * @author chad takahashi
 */
public final class JsArrayParser {
  private static Date javaDate = new Date(); // scratch date

  /**
   * Parses the specified jsArray object into a double[] array.
   */
  public double[] parse(JsonArrayNumber jsArray) {
    return parse(jsArray, 1.0);
  }

  /**
   * Parses the specified jsArray object into a double[] array, and then
   * multiplies each value in the resulting array by the specified multiplier.
   */
  public double[] parse(JsonArrayNumber jsArray, double multiplier) {
    ArgChecker.isNotNull(jsArray, "jsArray");

    final int len = jsArray.length();
    double aVal[] = new double[len];
    for (int i = 0; i < len; i++) {
      aVal[i] = jsArray.get(i) * multiplier;
    }
    return aVal;
  }

  /**
   * Parses an array of date-formatted strings into an array of timestamps.
   */
  public double[] parseFromDate(JsonArrayString jsArray, String dtformat) {
    ArgChecker.isNotNull(jsArray, "jsArray");
    ArgChecker.isNotNull(dtformat, "dtformat");
    DateFormatterFactory dff = DateFormatterFactory.getInstance();

    DateFormatter df = dff.getDateFormatter(dtformat);
    final int len = jsArray.length();
    ChronoDate chronoDate = ChronoDate.get(2000, 0, 1);

    double aVal[] = new double[len];
    for (int i = 0; i < len; i++) {
      javaDate.setTime((long) df.parse(jsArray.get(i)));
      chronoDate.set().year(javaDate.getYear() + 1900)
          .month(javaDate.getMonth()).day(javaDate.getDate())
          .hour(javaDate.getHours()).min(javaDate.getMinutes())
          .sec(javaDate.getSeconds()).done();
      aVal[i] = chronoDate.getTime();
    }
    return aVal;
  }
}
