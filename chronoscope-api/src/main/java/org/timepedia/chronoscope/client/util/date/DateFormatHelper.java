package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.MathUtil;


/**
 * Utilities for formatting a {@link ChronoDate} into different date
 * representations.
 */
public final class DateFormatHelper {

  public static DateFormatter getDateFormatter(String format) {
    return DateFormatterFactory.getInstance().getDateFormatter(format);
  }

  public static final DateFormatter hourMinuteSecFormatter = getDateFormatter("HH:mm:ss");
  public static final DateFormatter hourMinuteFormatter = getDateFormatter("HH:mm");
  public static final DateFormatter hourFormatter = getDateFormatter("HH");
  public static final DateFormatter dayFormatter = getDateFormatter("dd");
  public static final DateFormatter monthFormatter = getDateFormatter("MMM");
  public static final DateFormatter monthDayFormatter = getDateFormatter("MMM-dd");
  public static final DateFormatter yearMonthDayFormatter = getDateFormatter("yyyy-MMM-dd");
  public static final DateFormatter yearMonthFormatter = getDateFormatter("yyyy-MMM");
  public static final DateFormatter yearFormatter = getDateFormatter("yyyy");
  public static final DateFormatter twoDigitYearFormatter = getDateFormatter("yy");
  
  public String hourMinuteSec(ChronoDate d) {
    return hourMinuteSec(d.getTime());
  }

  public String hourMinuteSec(double d) {
    return hourMinuteSecFormatter.format(d);
  }

  public String hourMinute(ChronoDate d) {
    return hourMinute(d.getTime());
  }

  public String hourMinute(double d) {
    return hourMinuteFormatter.format(d);
  }
  
  public String hour(ChronoDate d) {
    return hour(d.getTime());
  }

  public String hour(double d) {
    return hourFormatter.format(d);
  }
  public String day(ChronoDate d) {
    return day(d.getTime());
  }

  public String day(double d) {
    return dayFormatter.format(d);
  }
  public String monthDay(ChronoDate d) {
    return monthDay(d.getTime());
  }

  public String monthDay(double d) {
    return monthDayFormatter.format(d);
  }
  public String yearMonthDay(ChronoDate d) {
    return yearMonthDay(d.getTime());
  }

  public String yearMonthDay(double d) {
    return yearMonthDayFormatter.format(d);
  }
  
  public String yearMonth(ChronoDate d) {
    return yearMonth(d.getTime());
  }

  public String yearMonth(double d) {
    return yearMonthFormatter.format(d);
  }
  
  public String year(ChronoDate d) {
    return year(d.getTime());
  }

  public String year(double d) {
    return yearFormatter.format(d);
  }
  
  public String twoDigitYear(ChronoDate d) {
    return twoDigitYear(d.getTime());
  }
  
  public String twoDigitYear(double d) {
    return twoDigitYearFormatter.format(d);
  }

  /**
   * Formats year and week as "yyyy-" (e.g. "Aug-23").
   *
   * @param d - The date to be formatted
   */
  public String yearAndWeek(ChronoDate d) {
    return d.getYear() + "'" + d.getWeekOfYear();
  }

  /**
   * Returns a 0-padded 2-digit number from the specified integer (e.g. pad(6)
   * returns "06", pad(59) returns "59").
   */
  public String pad(int num) {
    return num < 10 ? "0" : "" + num;
  }

  /**
   * Formats hour:minute:second:1/10sec (e.g. "23:56:05:04").
   *
   * @param d - The date to be formatted
   */
  public String tenthOfSecond(ChronoDate d) {
    int tenthSecond = MathUtil.mod((int) d.getTime() / 100, 10);
    return hourMinuteSec(d) + "." + pad(tenthSecond);
  }


}
