package org.timepedia.chronoscope.client.util;

import com.google.gwt.i18n.client.TimeZone;

/**
 * Interface used to switch between GWT and JDK date formatter implementations
 */
public interface DateFormatter {

  String format(double timestamp);
  
  String format(double timestamp, TimeZone timeZone);

  double parse(String date);
}
