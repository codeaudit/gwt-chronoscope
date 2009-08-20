package org.timepedia.chronoscope.client.util;

/**
 * Interface used to switch between GWT and JDK date formatter implementations
 */
public interface DateFormatter {

  String format(double timestamp);

  double parse(String date);
}
