package org.timepedia.chronoscope.client.util.date;

import org.timepedia.chronoscope.client.util.TimeUnit;

/**
 * Facilitates setting of date fields via method chaining.
 * 
 * @author chad takahashi
 */
public class DateFieldSetter {
  DateFields dateFields = new DateFields();
  private FastChronoDate parent;
  
  DateFieldSetter(FastChronoDate parent) {
    this.parent = parent;
  }
  
  public DateFieldSetter year(int year) {
    dateFields.year = year;
    return this;
  }
  
  public DateFieldSetter month(int month) {
    dateFields.month = month;
    return this;
  }
  
  public DateFieldSetter day(int day) {
    dateFields.day = day;
    return this;
  }
  
  public DateFieldSetter hour(int hour) {
    dateFields.hour = hour;
    return this;
  }
  
  public DateFieldSetter min(int minute) {
    dateFields.minute = minute;
    return this;
  }
  public DateFieldSetter sec(int second) {
    dateFields.second = second;
    return this;
  }
  public DateFieldSetter ms(int ms) {
    dateFields.ms = ms;
    return this;
  }
  
  public DateFieldSetter timeUnit(TimeUnit timeUnit, int value) {
    switch (timeUnit) {
      case YEAR:
        year(value); break;
      case MONTH:
        month(value); break;
      case DAY: 
        day(value); break;
      case HOUR:
        hour(value); break;
      case MIN:
        min(value); break;
      case SEC:
        sec(value); break;
      case MS:
        ms(value); break;
      default:
        throw new UnsupportedOperationException("TimeUnit '" + timeUnit + " not supported");
    }
    return this;
  }

  public void done() {
    // TODO: signal to parent I'm done.
    parent.commitDateFieldChanges();
  }
  
}
