package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.Arrays;

/**
 * @author chad takahashi
 */
class GregorianEraCalc extends EraCalc {
  
  /**
   * Array of pre-computed leap year indicator flags from year offsets [0..399].
   */
  private static final boolean[] LEAP_YEAR_FLAGS = calcLeapYearFlags();
  
  private static final double MS_IN_LEAPYEAR = FastChronoDate.MS_IN_LEAPYEAR;
  
  private static final double MS_IN_YEAR = FastChronoDate.MS_IN_YEAR;
  
  private static final double MS_IN_LEAP_CENTURY = 
      (MS_IN_LEAPYEAR + (MS_IN_YEAR * 3)) * 25; 
  
  private static final double MS_IN_NON_LEAP_CENTURY = 
      (MS_IN_YEAR * 4) + (MS_IN_LEAPYEAR + (MS_IN_YEAR * 3)) * 24; 
  
  private static final double MS_IN_4_CENTURY_PERIOD = 
      (MS_IN_LEAP_CENTURY + (MS_IN_NON_LEAP_CENTURY * 3));
  
  private static final double[] YR_OFFSETS_IN_MS = calcYearOffsetsInMillis(LEAP_YEAR_FLAGS);
  
  private static final int[] YR_OFFSETS_IN_DAYS = calcYearOffsetsInDays(LEAP_YEAR_FLAGS);
  
  int minYear = 1950;
  private double minTimeStamp = getJavaTimestamp(1950); 

  int maxYear = 2999;
  private double maxTimeStamp = getJavaTimestamp(maxYear + 1);

  private int maxLeapCentury = 2800;
  private double maxLeapCenturyTimestamp = getJavaTimestamp(maxLeapCentury);
  
  /**
   * Initializes this object to handle calculations for the specified year range.
   */
  public GregorianEraCalc init(int minYear, int maxYear) {
    if (minYear > maxYear) {
      throw new IllegalArgumentException("minYear > maxYear; minYear=" + 
          minYear + ", maxYear=" + maxYear);
    }
    
    this.minYear = minYear;
    this.minTimeStamp = getJavaTimestamp(minYear);
    this.maxYear = maxYear;
    this.maxTimeStamp = getJavaTimestamp(maxYear + 1);
    this.maxLeapCentury = (maxYear / 400) * 400;
    this.maxLeapCenturyTimestamp = getJavaTimestamp(maxLeapCentury);
    
    return this;
  }
  
  @Override
  public double calcYearField(double timeInMs, DateFields dateFields) {
    checkTimestampNotLessThan(timeInMs, this.minTimeStamp);
    checkTimestampLessThan(timeInMs, this.maxTimeStamp);
    
    // Calculate the starting point of the 4-century period that contains 'timeInMs'
    final double normalizedMs = timeInMs - maxLeapCenturyTimestamp;
    final int fourCenturyIndex = (int)Math.floor(normalizedMs / MS_IN_4_CENTURY_PERIOD);
    final int yearStart = maxLeapCentury + (fourCenturyIndex * 400);
    final double fourCenturyPeriodStart = 
        maxLeapCenturyTimestamp + (fourCenturyIndex * MS_IN_4_CENTURY_PERIOD);

    // Calculate 1) year and 2) year offset in milliseconds
    final double fourCenturyPeriodOffset = timeInMs - fourCenturyPeriodStart;
    final int yearOffset = findNearestYearOffset(fourCenturyPeriodOffset);
    dateFields.year = yearStart + yearOffset;
    return fourCenturyPeriodOffset - YR_OFFSETS_IN_MS[yearOffset];
  }

  @Override
  public double calcYearTimestamp(int year) {
    if (year < this.minYear) {
      throw new IllegalArgumentException(this.minYear + " is the smallest supported year");
    }
    else if (year > this.maxYear) {
      throw new IllegalArgumentException(this.maxYear + " is the largest supported year");
    }
    
    final int yearDiff = maxLeapCentury - year;
    double ts = maxLeapCenturyTimestamp;
    ts -= (Math.ceil((double)yearDiff / 400.0) * MS_IN_4_CENTURY_PERIOD);

    int yearOffset = year % 400;
    ts += YR_OFFSETS_IN_MS[yearOffset];
    
    return ts;
  }

  @Override
  public int getMaxYear() {
    return this.maxYear;
  }
  
  @Override
  public boolean isLeapYear(int year) {
    return LEAP_YEAR_FLAGS[MathUtil.mod(year, 400)];
  }
  
  @Override
  public DayOfWeek calcDayOfWeek(int year, int month, int day) {
    int yearMod400 = MathUtil.mod(year, 400);
    int numDaysFromPeriodStart = YR_OFFSETS_IN_DAYS[yearMod400];
    numDaysFromPeriodStart += this.getMonthOffsetsInDays(isLeapYear(year))[month];
    numDaysFromPeriodStart += (day - 1);
    
    // NOTE: 6 = SATURDAY.  The '6' in the formula below is needed because the 1st day
    // in each 4-century period begins on a Saturday.
    int dayOfWeekIndex = MathUtil.mod(numDaysFromPeriodStart + 6, 7);
    return FastChronoDate.DAYS_OF_WEEK[dayOfWeekIndex];
  }
  
  /**
   * Returns an array containing 4 centuries of year offset timestamps in milliseconds.
   * Element [0] = 0, representing Jan-01 at midnight for the first year; 
   * element [1] = the timestamp for Jan-02 at midnight, and so on.
   * <p>
   * A period of 4 centuries was chosen because that's the smallest interval that repeats
   * indefinitely according to the Gregorian calendar.
   */
  private static double[] calcYearOffsetsInMillis(boolean[] leapYearFlags) {
    double[] offsets = new double[400];
    offsets[0] = 0;
    
    int counter = 0;
    int i = 1;
    while (i < offsets.length) {
      boolean isLeapYear = leapYearFlags[counter];
      offsets[i] = offsets[i - 1] + (isLeapYear ? MS_IN_LEAPYEAR : MS_IN_YEAR);
      ++counter;
      ++i;
    }
    
    return offsets;
  }
  
  /**
   * Returns an array containing 4 centuries of year offsets in days.
   * Element [0] = 0, meaning that 0 days have passed since the first day of the 4 century
   * period (obviously); element [1] = the number of days that have passed in 1 year
   * from the 4 century period, and so on.
   * <p>
   * A period of 4 centuries was chosen because that's the smallest interval that repeats
   * indefinitely according to the Gregorian calendar.
   */
  private static int[] calcYearOffsetsInDays(boolean[] leapYearFlags) {
    int[] offsets = new int[400];
    offsets[0] = 0;
    
    int counter = 0;
    int i = 1;
    while (i < offsets.length) {
      boolean isLeapYear = leapYearFlags[counter];
      offsets[i] = offsets[i - 1] + (isLeapYear ? 366 : 365);
      ++counter;
      ++i;
    }
    
    return offsets;
  }

  /**
   * Calculates an array containing 4 centuries of leap year flags.
   */
  private static boolean[] calcLeapYearFlags() {
    boolean[] leapYearFlags = new boolean[400];
    for (int y = 0; y < leapYearFlags.length; y++) {
      leapYearFlags[y] = (y == 0) || (y % 4 == 0 && y % 100 != 0);
    }
    return leapYearFlags;
  }

  /**
   * Finds the largest year offset index whose asssociated  millisecond value is less
   * than or equal to <tt>ts</tt>.
   */
  private final int findNearestYearOffset(double ts) {
    int index = Arrays.binarySearch(YR_OFFSETS_IN_MS, ts);
    if (index >= 0) { // key was found
      return index;
    } 
    
    if (index == -1) {
      return 0;
    }
    
    return (-index) - 2; // See javadocs for binarySearch...
  }
  
}
