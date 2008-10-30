package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;

import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

import java.util.Date;

/**
 * Parses JsArray objects into a primitive java <tt>double[]</tt> array.
 * 
 * @author chad takahashi
 */
public final class JsArrayParser {

  /**
   * Parses the specified jsArray object into a double[] array.
   */
  public double[] parse(JsArrayNumber jsArray) {
    return parse(jsArray, 1.0);
  }

  /**
   * Parses the specified jsArray object into a double[] array, and then
   * multiplies each value in the resulting array by the specified multiplier.
   */
  public double[] parse(JsArrayNumber jsArray, double multiplier) {
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
  public double[] parseFromDate(JsArrayString jsArray, String dtformat) {
    ArgChecker.isNotNull(jsArray, "jsArray");
    ArgChecker.isNotNull(dtformat, "dtformat");
    
    DateTimeFormat df = DateTimeFormat.getFormat(dtformat);
    final int len = jsArray.length();
    ChronoDate chronoDate = ChronoDate.get(2000, 0, 1);

    double aVal[] = new double[len];
    for (int i = 0; i < len; i++) {
      Date javaDate = df.parse(jsArray.get(i));
      chronoDate.set(TimeUnit.YEAR, javaDate.getYear() + 1900);
      chronoDate.set(TimeUnit.MONTH, javaDate.getMonth());
      chronoDate.set(TimeUnit.DAY, javaDate.getDate());
      aVal[i] = chronoDate.getTime();
    }
    return aVal;
  }
}
