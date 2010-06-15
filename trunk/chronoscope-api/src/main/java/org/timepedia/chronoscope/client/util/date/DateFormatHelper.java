package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.DateFormatter;

import java.util.Date;


/**
 * Utilities for formatting a {@Link ChronoDate} into different date
 * representations.
 *
 * @author chad takahashi
 */
public final class DateFormatHelper {

  static final String[] MONTH_LABELS = createMonthLabels();

  // Used by pad(int) to efficiently convert ints in the range [0..59] to a
  // zero-padded 2-digit string.
  static final String[] TWO_DIGIT_NUMS = new String[]{"00", "01", "02", "03",
      "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
      "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
      "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
      "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51",
      "52", "53", "54", "55", "56", "57", "58", "59",};

  private static final String[] HOURS_OF_DAY = createHoursOfDayLabels();

  
  
  public static DateFormatter getDateFormatter(String format) {
    return DateFormatterFactory.getInstance().getDateFormatter(format);
  }

  private static final DateFormatter hourFormatter = getDateFormatter("H");
  private static final DateFormatter hourMinuteFormatter = getDateFormatter("H:mm");
  private static final DateFormatter hourMinuteSecFormatter = getDateFormatter("H:mm:ss");
  

  /**
   * Uses GWT DateTimeFormat class to obtain hour-of-day labels (e.g. "9am").
   */
  private static String[] createHoursOfDayLabels() {
    DateFormatter fmt = getDateFormatter("H"); // h=hour, a=AM/PM
    String[] hourLabels = new String[24];
    for (int h = 0; h < hourLabels.length; h++) {
      int hr = h;
      if (hr < 0) { hr = 23; }
      hourLabels[h] = fmt
          .format(new Date(1990 - 1900, 0, 1, hr, 0, 0).getTime());
    }
    return hourLabels;
  }

  /**
   * Uses GWT DateTimeFormat class to obtain abbreviated month labels to ensure
   * local-specificity.
   */
  private static String[] createMonthLabels() {
    DateFormatter fmt = getDateFormatter("MMM"); // "Jan", "Feb", ...
    String[] monthLabels = new String[12];
    for (int m = 0; m < monthLabels.length; m++) {
      monthLabels[m] = fmt.format(new Date(1970 - 1970, m, 1).getTime());
    }
    return monthLabels;
  }

  /**
   * Returns an abbreviated month string for the specified date.
   */
  private static String formatMonth(ChronoDate d) {
    return MONTH_LABELS[d.getMonth()];
  }

  /**
   * Formats day and month as "dd-MMM" (e.g. "31-Oct").
   *
   * @param d - The date to be formatted
   */
  public String dayAndMonth(ChronoDate d) {
    return pad(d.getDay()) + "-" + formatMonth(d);
  }

  /**
   * Formats the hour of the day.
   *
   * @param hourOfDay - a value in the range [0, 23]
   */
  public String hour(int hourOfDay) {
    return HOURS_OF_DAY[hourOfDay];
    // return pad(hourOfDay) + ":00";
  }
  
  /**
   * Formats the hour of the day.
   *
   * @param hourOfDay - a value in the range [0, 23]
   */
  public String slowHour(ChronoDate d) {
    return hourFormatter.format(d.getTime());
  }

  /**
   * Formats hour and minute as "hh:mm".
   *
   * @param d - The date to be formatted
   */
  public String hourAndMinute(ChronoDate d) {
    return hourMinuteFormatter.format(d.getTime());
  }

  /**
   * Formats hour minute and second as "hh:mm:ss".
   *
   * @param d - The date to be formatted
   */
  public String hourMinuteSecond(ChronoDate d) {
    return hourMinuteSecFormatter.format(d.getTime());
  }

  /**
   * Formats month and year as "mmm'yy" (e.g. "Aug-23").
   *
   * @param d - The date to be formatted
   */
  public String monthAndYear(ChronoDate d) {
    return formatMonth(d) + "'" + twoDigitYear(d);
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
    return TWO_DIGIT_NUMS[num];
    // return num < 10 ? "0" + num : "" + num;
  }

  /**
   * Formats hour:minute:second:1/10sec (e.g. "23:56:05:04").
   *
   * @param d - The date to be formatted
   */
  public String tenthOfSecond(ChronoDate d) {
    int tenthSecond = MathUtil.mod((int) d.getTime() / 100, 10);
    return hourMinuteSecond(d) + "." + pad(tenthSecond);
  }

  /**
   * Formats a 2 digit year string from the specified date. For example, the
   * date '2006-05-25' returns the string "06".
   */
  public String twoDigitYear(ChronoDate d) {
    String yr = String.valueOf(d.getYear());
    return yr.substring(yr.length() - 2);
  }
}
