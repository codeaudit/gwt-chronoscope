package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.TimeUnit;

import java.util.Date;

/**
 * {@link ChronoDate} backed by a <tt>java.util.Date</tt> object.
 * 
 * @author chad takahashi
 */
public final class DefaultChronoDate extends ChronoDate {
  private Date d;
  
  public DefaultChronoDate(double timeStamp) {
    this.d = new Date((long)timeStamp);
  }

  @Override
  public void add(TimeUnit timeUnit, int numUnits) {
    switch (timeUnit) {
      case YEAR:
        d.setYear(d.getYear() + numUnits);
        break;
      case MONTH:
        d.setMonth(d.getMonth() + numUnits);
        break;
      case WEEK:
        d.setDate(d.getDate() + numUnits * 7);
        break;
      case DAY:
        d.setDate(d.getDate() + numUnits);
        break;
      case HOUR:
        d.setHours(d.getHours() + numUnits);
        break;
      case MIN:
        d.setMinutes(d.getMinutes() + numUnits);
        break;
      case SEC:
        d.setSeconds(d.getSeconds() + numUnits);
        break;
      case TENTH_SEC:
        d.setTime(d.getTime() + (long)(numUnits * 100));
        break;
      default:
        throw new UnsupportedOperationException("TimeUnit " + timeUnit + " not supported at this time");
    }
  }

  @Override
  public int getDaysInMonth() {
    int year = d.getYear() + 1900;
    int month = d.getMonth();
    
    // Special case: Oct 1582 only has 21 days (Oct 5th - 14th were omitted due to 
    // transition from Julian to Gregorian calendar).
    if (year == 1582 && month == 9) {
      return 21;
    }
    
    Date tmp = (Date)d.clone();
    tmp.setMonth(month + 1);
    tmp.setDate(1);
    tmp.setDate(0);
    return tmp.getDate();
  }
  
  @Override
  public DayOfWeek getDayOfWeek() { // FIXME
    DayOfWeek dow = DayOfWeek.MONDAY;
    switch(d.getDay()){
      case 0: dow = DayOfWeek.SUNDAY; break;
      case 1: dow = DayOfWeek.MONDAY; break;
      case 2: dow = DayOfWeek.TUESDAY; break;
      case 3: dow = DayOfWeek.WEDNESDAY; break;
      case 4: dow = DayOfWeek.THURSDAY; break;
      case 5: dow = DayOfWeek.FRIDAY; break;
      case 6: dow = DayOfWeek.SATURDAY; break;
    }
    return dow;
  }

  @Override
  public int getDayOfYear() {
    int[] ordMonth = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    int[] ordMonthLeap = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    int ordinal = isLeapYear() ? ordMonthLeap[this.getMonth()] : ordMonth[this.getMonth()];

    return ordinal += this.getDay();
  }

  @Override
  public int getWeekOfYear() {
    int daysInYear = isLeapYear()? 366 : 365;
    int ordinal = getDayOfYear();
    int weekday = isoWeekday(getDayOfWeek());
    int week =  (ordinal - isoWeekday(getDayOfWeek()) + 10)/7;
    if (53 == week) { // check that it's not in W1 of year++
      if ((daysInYear - ordinal) < (4 - weekday)) week = 1;
    } else if (0 == week) week = 53;  // W0 => W53 of year--
    return week;
  }

  @Override
  public int getDay() {
    return d.getDate();
  }
  
  @Override
  public int getHour() {
    return d.getHours();
  }
  
  @Override
  public int getMinute() {
    return d.getMinutes();
  }
  
  @Override
  public int getMonth() {
    return d.getMonth();
  }

  @Override
  public int getSecond() {
    return d.getSeconds();
  }

  @Override
  public double getTime() {
    return (double)d.getTime();
  }

  @Override
  public int getYear() {
    return d.getYear() + 1900;
  }
  
  public boolean isFirstOfMonth() {
    return d.getDate() == 1;
  }

  @Override
  public ChronoDate truncate(TimeUnit timeUnit) {
    Date maskedDate;
    switch (timeUnit) {
      case YEAR:
        maskedDate = new Date(d.getYear(), 0, 1);
        break;
      case MONTH:
        maskedDate = new Date(d.getYear(), d.getMonth(), 1);
        break;
      case WEEK:
        FastChronoDate fd=new FastChronoDate(d.getTime());
        fd.truncate(TimeUnit.WEEK);
        maskedDate = new Date((long)fd.getTime());
        break;
      case DAY:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate());
        break;
      case HOUR:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), 0, 0);
        break;
      case MIN:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), 0);
        break;
      case SEC:
        maskedDate = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
        break;
      default:
        throw new IllegalArgumentException("Unsupported time unit: " + timeUnit);
    }
    
    this.d = maskedDate;
    return this;
  }

  @Override
  public DateFieldSetter set() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(TimeUnit timeUnit, int value) {
    switch (timeUnit) {
      case YEAR:
        d.setYear(value - 1900); 
        break;
      case MONTH:
        d.setMonth(value); 
        break;
      case WEEK:
        FastChronoDate fd=new FastChronoDate(d.getTime());
        fd.set(timeUnit, value);
        d.setTime((long) fd.getTime());
        break;
      case DAY:
        d.setDate(value);
        break;
      case HOUR:
        d.setHours(value);
        break;
      case MIN:
        d.setMinutes(value);
        break;
      case SEC:
        d.setSeconds(value);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported time unit: " + timeUnit);
    }
  }

  @Override
  public void setTime(double ms) {
    d.setTime((long)ms);
  }
  
  @Override
  public String toString() {
    return this.d.toString();
  }

  @Override
  public boolean isLeapYear() {
    int year = this.getYear();
    return  (0 == year % 4) && (0 != year % 100) || (0 == year % 400);
  }

}
