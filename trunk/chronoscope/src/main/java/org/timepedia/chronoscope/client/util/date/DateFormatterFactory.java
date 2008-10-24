package org.timepedia.chronoscope.client.util.date;

import com.google.gwt.widgetideas.datepicker.client.DateBox;

import org.timepedia.chronoscope.client.util.DateFormatter;

/**
*/
public abstract class DateFormatterFactory {

  static DateFormatterFactory dateFormatterFactory;

  public abstract DateFormatter getDateFormatter(String format);

  public static void setDateFormatterFactory(
      DateFormatterFactory dff) {
    dateFormatterFactory = dff;
  }

  public static DateFormatterFactory getInstance() {
    return dateFormatterFactory;
  }
}
