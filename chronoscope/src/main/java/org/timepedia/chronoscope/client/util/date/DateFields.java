/**
 * 
 */
package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.TimeUnit;

/**
 * Simple structure that holds the constituent date fields that compose a complete date
 * down to millisecond precision.
 * 
 * @author chad takahashi
 */
class DateFields {
  
  public int year, month, day, hour, minute, second, ms;
  
  public String toString() {
    return year + "-" + DateFormatHelper.MONTH_LABELS[month] + "-"
        + DateFormatHelper.TWO_DIGIT_NUMS[day] + " "
        + DateFormatHelper.TWO_DIGIT_NUMS[hour] + ":"
        + DateFormatHelper.TWO_DIGIT_NUMS[minute] + ":"
        + DateFormatHelper.TWO_DIGIT_NUMS[second] + "." + ms;

  }
  
  /**
   * Resets all date fields to default values
   */
  public DateFields clear() {
    this.year = 0;
    this.month = 0;
    this.day = 1;
    this.hour = 0;
    this.minute = 0;
    this.second = 0;
    this.ms = 0;
    return this;
  }
  
  /**
   * Resets all date fields after <tt>timeUnit</tt> to 0.
   */
  public void clearStartingAfter(TimeUnit timeUnit) {
    switch (timeUnit) {
      case YEAR:
        this.month = 0;
      case MONTH:
        this.day = 1;
      case DAY:
        this.hour = 0;
      case HOUR:
        this.minute = 0;
      case MIN:
        this.second = 0;
      case SEC:
        this.ms = 0;
        break;
      default:
        throw new UnsupportedOperationException(timeUnit + " time unit not applicable");
    }
  }
  
  public DateFields setYear(int year) {
    this.year = year;
    return this;
  }
  
  public DateFields setMonth(int month) {
    this.month = month;
    return this;
  }
  
  public DateFields setDay(int day) {
    this.day = day;
    return this;
  }
}
