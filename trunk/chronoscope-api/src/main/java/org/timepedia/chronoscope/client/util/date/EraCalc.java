package org.timepedia.chronoscope.client.util.date;

import java.util.Date;

/**
 * Encapsulates era-specific calculations.
 * 
 * @author chad takahashi
 */
public abstract class EraCalc {
  private static Date scratchDate = new Date(0,0,1);
  static final GregorianConstants gregorianConstants = new GregorianConstants();
  
  private static final int[] DAYS_IN_MONTH_LEAPYEAR = {
      31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  private static final int[] DAYS_IN_MONTH_NON_LEAPYEAR = {
      31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  private static final EraCalc GREGORIAN_ERA_CALC = new GregorianEraCalc(1583, 1999999);
  //private static final EraCalc GREGORIAN_ERA_CALC = new GregorianEraCalc().init(1583, 12999);
  private static final EraCalc JULIAN_ERA_CALC = new JulianEraCalc();
  private static final EraCalc Y1582_ERA_CALC = new JulianCrossoverEraCalc();
  private static final double TS_1582_JAN_01 = getTimestampForYear(1582);
  private static final double TS_1583_JAN_01 = getTimestampForYear(1583);

  /**
   * Same as {@link #monthOffsetsInMs} and {@link #monthOffsetsInMsLeapYear},
   * except offset intervals are in days instead of milliseconds.
   */
  protected int[] monthOffsetsInDays, monthOffsetsInDaysLeapYear;

  /**
   * Holds month offsets in milliseconds for every month in a non-leap year and
   * leap year. [0] = # ms from Jan-01 at midnight to Jan-01 at midnight; [1] = #
   * ms from Jan-01 at midnight to Feb-01 at midnight; ...
   */
  protected double[] monthOffsetsInMs, monthOffsetsInMsLeapYear;

  /**
   * Returns a singleton {@link EraCalc} object capable of handling date
   * calculations for the specified timestamp.
   */
  public static final EraCalc getByTimestamp(double ts) {
    if (ts >= TS_1583_JAN_01) {
      return GREGORIAN_ERA_CALC;
    } else if (ts >= TS_1582_JAN_01) {
      return Y1582_ERA_CALC;
    } else {
      return JULIAN_ERA_CALC;
    }
  }

  /**
   * Returns a singleton {@link EraCalc} object capable of handling date
   * calculations for the specified year.
   */
  public static final EraCalc getByYear(int year) {
    if (year >= 1583) {
      return GREGORIAN_ERA_CALC;
    } else if (year >= 1582) {
      return Y1582_ERA_CALC;
    } else {
      return JULIAN_ERA_CALC;
    }
  }

  static double[] calcMonthOffsetsInMs(int[] daysInMonthArray) {
    double[] monthOffsets = new double[13];
    monthOffsets[0] = 0.0;
    for (int i = 0; i < 12; i++) {
      int daysInMonth = daysInMonthArray[i];
      monthOffsets[i + 1] = monthOffsets[i]
          + (daysInMonth * FastChronoDate.MS_IN_DAY);
    }
    return monthOffsets;
  }

  /**
   * @throws UnsupportedOperationException if <tt>actualTimestamp</tt> is
   *           equal to or greater than <tt>maxTimestamp</tt>.
   */
  static void checkTimestampLessThan(double actualTimestamp, double maxTimestamp) {
    if (actualTimestamp >= maxTimestamp) {
      throw new UnsupportedOperationException("actualTimestamp too big: "
          + (long) actualTimestamp);
    }
  }

  /**
   * @throws UnsupportedOperationException if <tt>actualTimestamp</tt> is less
   *           than <tt>minTimestamp</tt>.
   */
  static void checkTimestampNotLessThan(double actualTimestamp, double minTimestamp) {
    if (actualTimestamp < minTimestamp) {
      throw new UnsupportedOperationException("actualTimestamp too small: "
          + (long) actualTimestamp);
    }
  }

  /**
   * Returns a timestamp created by java.utilDate for Jan-01 at midnight for the
   * specified year.
   */
  static double getTimestampForYear(int year) {
    scratchDate.setMonth(0);
    scratchDate.setDate(1);
    if (year < 2000) {
      // Date.UTC(1,2,3,4,5,6);  // TODO - unused, but UTC vs local issue ?
      scratchDate.setYear(year-1900);
      return scratchDate.getTime();
    }
    
    final double numMillisIn4centuries = 
          gregorianConstants.msIn4centuryPeriod;
    scratchDate.setYear(2000-1900);
    double y2k_timestamp = scratchDate.getTime();

    final int numLeapCenturies = (year - 2000) / 400;
    final int nearestLeapCentury = 2000 + (numLeapCenturies * 400);
    final int leapCenturyOffset = year - nearestLeapCentury;
    double offset = numMillisIn4centuries * numLeapCenturies;
    offset += gregorianConstants.yearOffsetsInMs[leapCenturyOffset];
    
    return y2k_timestamp + offset;
  }

  /**
   * Returns an array containing the number of days from Jan-01 to the 1st day
   * of each month. E.g. [0] = 0, indicating that 0 days have passed from Jan-01
   * to Jan-01; [1] = 31, meaning that 31 days pass from Jan-01 to Feb-01, and
   * so on.
   */
  static final int[] calcMonthOffsetsInDays(int[] daysInMonthArray) {
    int[] a = new int[daysInMonthArray.length];
    a[0] = 0;
    for (int i = 0; i < 11; i++) {
      a[i + 1] = a[i] + daysInMonthArray[i];
    }
    return a;
  }
  
  public EraCalc() {
    this.monthOffsetsInMs = calcMonthOffsetsInMs(DAYS_IN_MONTH_NON_LEAPYEAR);
    this.monthOffsetsInMsLeapYear = calcMonthOffsetsInMs(DAYS_IN_MONTH_LEAPYEAR);
    
    this.monthOffsetsInDays = calcMonthOffsetsInDays(DAYS_IN_MONTH_NON_LEAPYEAR);
    this.monthOffsetsInDaysLeapYear = calcMonthOffsetsInDays(DAYS_IN_MONTH_LEAPYEAR);
  }

  public int calcDayOfYear(int year, int month, int day) {
    int ordinal = isLeapYear(year) ?
      this.monthOffsetsInDaysLeapYear[month] : this.monthOffsetsInDays[month];
    return ordinal += day;
  }

  public static int isoWeekday(DayOfWeek weekday) {
    int dow = 0;
    switch(weekday) {
      case MONDAY: dow = 1; break;
      case TUESDAY: dow = 2; break;
      case WEDNESDAY: dow = 3; break;
      case THURSDAY: dow = 4; break;
      case FRIDAY: dow = 5; break;
      case SATURDAY: dow = 6; break;
      case SUNDAY: dow = 7; break;
    }
    return dow;
  }

  /**
   * Determines the day of the week (Sun, Mon, Tue, etc.) from the specified
   * date.
   */
  public abstract DayOfWeek calcDayOfWeek(int year, int month, int day);

  /**
   * Determines the ISO week of the year (1-53) from the specified date.
   */
  public int calcWeekOfYear(int year, int month, int day) {
    int daysInYear = isLeapYear(year)? 366 : 365;
    int ordinal = calcDayOfYear(year, month, day);
    int weekday = isoWeekday(calcDayOfWeek(year, month, day));
    int week = (ordinal - weekday + 10)/7;
    if (53 == week) { // check that it's not in W1 of year++ 
      if ((daysInYear - ordinal) < (4 - weekday)) week = 1;
    } else if (0 == week) week = 53;
    return (0 == week) ? 53 : week; // W0 => W53 of year--    
  }

  /**
   * Calculates and sets {@link DateFields#year} based on <tt>timeInMs</tt>.
   * 
   * @return the millisecond remainder of <tt>timeInMs</tt> from the beginning
   *         of the calculated year.
   */
  public abstract double calcYearField(double timeInMs, DateFields dateFields);

  /**
   * Calculates the timestamp corresponding to Jan-01 00h:00m:00s.0ms for the
   * specified year.
   */
  public abstract double calcYearTimestamp(int year);

  /**
   * Returns the number of days in the specified month, taking into account whether
   * or not the month's associated year is a leap year.
   */
  public int getDaysInMonth(int month, boolean isLeapYear) {
    if (isLeapYear) {
      return DAYS_IN_MONTH_LEAPYEAR[month];
    }
    else {
      return DAYS_IN_MONTH_NON_LEAPYEAR[month];
    }
  }

  /**
   * Returns the largest year that this {@link EraCalc} is capable of handling.
   */
  public abstract int getMaxYear();

  /**
   * Returns the millisecond offsets for the first day of each month within the
   * year.
   */
  public double[] getMonthOffsetsInMs(boolean isLeapYear) {
    return isLeapYear ? this.monthOffsetsInMsLeapYear : this.monthOffsetsInMs;
  }
  
  public int[] getMonthOffsetsInDays(boolean isLeapYear) {
    return isLeapYear ? this.monthOffsetsInDaysLeapYear : this.monthOffsetsInDays;
  }

  /**
   * Returns true only if the specified year is a leap year.
   */
  public abstract boolean isLeapYear(int year);

}
