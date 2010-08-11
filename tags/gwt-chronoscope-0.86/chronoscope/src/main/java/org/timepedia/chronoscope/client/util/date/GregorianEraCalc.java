package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.Arrays;

/**
 * @author chad takahashi
 */
public class GregorianEraCalc extends EraCalc {
  
  private static GregorianConstants constants = EraCalc.gregorianConstants;
  
  int minYear, maxYear, maxLeapCentury;
  private double minTimeStamp, maxTimeStamp, maxLeapCenturyTimestamp; 
  
  /**
   * Initializes this object to handle calculations for the specified year range.
   */
  public GregorianEraCalc(int minYear, int maxYear) {
    if (minYear > maxYear) {
      throw new IllegalArgumentException("minYear > maxYear; minYear=" + 
          minYear + ", maxYear=" + maxYear);
    }
    
    this.minYear = minYear;
    this.minTimeStamp = getTimestampForYear(minYear);
    this.maxYear = maxYear;
    this.maxTimeStamp = getTimestampForYear(maxYear + 1);
    this.maxLeapCentury = MathUtil.quantize(maxYear, 400);
    this.maxLeapCenturyTimestamp = getTimestampForYear(maxLeapCentury);
  }
  
  @Override
  public double calcYearField(double timeInMs, DateFields dateFields) {
    checkTimestampNotLessThan(timeInMs, this.minTimeStamp);
    checkTimestampLessThan(timeInMs, this.maxTimeStamp);
    
    // Calculate the starting point of the 4-century period that contains 'timeInMs'
    final double normalizedMs = timeInMs - maxLeapCenturyTimestamp;
    final int fourCenturyIndex = (int)Math.floor(normalizedMs / constants.msIn4centuryPeriod);
    final int yearStart = maxLeapCentury + (fourCenturyIndex * 400);
    final double fourCenturyPeriodStart = 
        maxLeapCenturyTimestamp + (fourCenturyIndex * constants.msIn4centuryPeriod);

    // Calculate 1) year and 2) year offset in milliseconds
    final double fourCenturyPeriodOffset = timeInMs - fourCenturyPeriodStart;
    final int yearOffset = findNearestYearOffset(fourCenturyPeriodOffset);
    dateFields.year = yearStart + yearOffset;
    return fourCenturyPeriodOffset - constants.yearOffsetsInMs[yearOffset];
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
    ts -= (Math.ceil((double)yearDiff / 400.0) * constants.msIn4centuryPeriod);

    int yearOffset = year % 400;
    ts += constants.yearOffsetsInMs[yearOffset];
    
    return ts;
  }

  @Override
  public int getMaxYear() {
    return this.maxYear;
  }
    
  /**
   * Returns the minimum supported year.
   */
  public int getMinYear() {
    return this.minYear;
  }

  @Override
  public boolean isLeapYear(int year) {
    return constants.leapYearFlags[MathUtil.mod(year, 400)];
  }
  
  @Override
  public DayOfWeek calcDayOfWeek(int year, int month, int day) {
    int yearMod400 = MathUtil.mod(year, 400);
    int numDaysFromPeriodStart = constants.yearOffsetsInDays[yearMod400];
    numDaysFromPeriodStart += this.getMonthOffsetsInDays(isLeapYear(year))[month];
    numDaysFromPeriodStart += (day - 1);
    
    // NOTE: 6 = SATURDAY.  The '6' in the formula below is needed because the 1st day
    // in each 4-century period begins on a Saturday.
    int dayOfWeekIndex = MathUtil.mod(numDaysFromPeriodStart + 6, 7);
    return FastChronoDate.DAYS_OF_WEEK[dayOfWeekIndex];
  }
  
  /**
   * Finds the largest year offset index whose asssociated  millisecond value is less
   * than or equal to <tt>ts</tt>.
   */
  private final int findNearestYearOffset(double ts) {
    int index = Arrays.binarySearch(constants.yearOffsetsInMs, ts);
    if (index >= 0) { // key was found
      return index;
    } 
    
    if (index == -1) {
      return 0;
    }
    
    return (-index) - 2; // See javadocs for binarySearch...
  }
  
}
