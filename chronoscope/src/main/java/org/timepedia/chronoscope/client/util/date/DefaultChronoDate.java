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
  public int get(TimeUnit timeUnit) {
    switch (timeUnit) {
      case YEAR:
        return d.getYear() + 1900;
      case MONTH:
        return d.getMonth();
      case DAY:
        return d.getDate();
      case HOUR:
        return d.getHours();
      case MIN:
        return d.getMinutes();
      case SEC:
        return d.getSeconds();
      default:
        throw new UnsupportedOperationException("TimeUnit " + timeUnit + " not supported at this time");
    }
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
    return d.getTime();
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
  public void set(TimeUnit timeUnit, int value) {
    switch (timeUnit) {
      case YEAR:
        d.setYear(value - 1900); 
        break;
      case MONTH:
        d.setMonth(value); 
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

}
