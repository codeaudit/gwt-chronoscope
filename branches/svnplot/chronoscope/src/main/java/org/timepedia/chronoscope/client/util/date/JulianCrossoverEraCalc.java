package org.timepedia.chronoscope.client.util.date;

/**
 * Only handles year 1582, which is the year in which the Julian calendar was replaced
 * by the Gregorian calendar.  This calendar has a 355-day year (October 5th through
 * October 14th were removed).
 * 
 * @author chad takahashi
 */
class JulianCrossoverEraCalc extends EraCalc {
  
  // Oct. is special month having only 21 days (the 5th through 14th are omitted)
  private static final int[] DAYS_IN_MONTH_1582 = 
      {31, 28, 31, 30, 31, 30, 31, 31, 30, 21 /* Oct. is missing 10 days */, 30, 31};

  private static final double TS_1582_JAN_01 = EraCalc.getJavaTimestamp(1582);
  
  private static final double TS_1583_JAN_01 = getJavaTimestamp(1583);

  public JulianCrossoverEraCalc() {
    this.monthOffsetsInMs = calcMonthOffsetsInMs(DAYS_IN_MONTH_1582);
    this.monthOffsetsInMsLeapYear = calcMonthOffsetsInMs(DAYS_IN_MONTH_1582);
    
    this.monthOffsetsInDays = calcMonthOffsetsInDays(DAYS_IN_MONTH_1582);
    this.monthOffsetsInDaysLeapYear = calcMonthOffsetsInDays(DAYS_IN_MONTH_1582);
  }
  
  @Override
  public double calcYearField(double timeInMs, DateFields dateFields) {
    checkTimestampNotLessThan(timeInMs, TS_1582_JAN_01);
    checkTimestampLessThan(timeInMs, TS_1583_JAN_01);
    
    dateFields.year = 1582;
    return timeInMs - TS_1582_JAN_01;
  }

  @Override
  public DayOfWeek calcDayOfWeek(int year, int month, int day) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double calcYearTimestamp(int year) {
    return TS_1582_JAN_01;
  }
  
  @Override
  public int getDaysInMonth(int month, boolean isLeapYear) {
    return DAYS_IN_MONTH_1582[month];
  }

  @Override
  public int getMaxYear() {
    return 1582;
  }

  @Override
  public boolean isLeapYear(int year) {
    return false;
  }
  
  @Override
  public double[] getMonthOffsetsInMs(boolean isLeapYear) {
    if (this.monthOffsetsInMs == null) {
      this.monthOffsetsInMs = calcMonthOffsetsInMs(DAYS_IN_MONTH_1582);
    }
    return this.monthOffsetsInMs;
  }

}
