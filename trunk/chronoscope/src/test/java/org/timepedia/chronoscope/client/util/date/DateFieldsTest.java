package org.timepedia.chronoscope.client.util.date;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.TimeUnit;

/**
 * @author chad takahashi
 */
public class DateFieldsTest extends TestCase {

  public void testCopyTo() {
    DateFields dfSource = getTestDateFields();
    
    DateFields dfTarget = new DateFields();
    dfSource.copyTo(dfTarget);
    
    assertEquals(dfSource.year, dfTarget.year);
    assertEquals(dfSource.month, dfTarget.month);
    assertEquals(dfSource.day, dfTarget.day);
    assertEquals(dfSource.hour, dfTarget.hour);
    assertEquals(dfSource.minute, dfTarget.minute);
    assertEquals(dfSource.second, dfTarget.second);
    assertEquals(dfSource.ms, dfTarget.ms);
  }
  
  public void testClear() {
    DateFields df = getTestDateFields();
    df.clear();
    assertEquals(0, df.year);
    assertEquals(0, df.month);
    assertEquals(1, df.day);
    assertEquals(0, df.hour);
    assertEquals(0, df.minute);
    assertEquals(0, df.second);
    assertEquals(0, df.ms);
  }

  public void testClearAfter() {
    testClearAfterYear(TimeUnit.DECADE, 1999, 1990);
    testClearAfterYear(TimeUnit.DECADE, 1419, 1410);
    testClearAfterYear(TimeUnit.DECADE, 1410, 1410);
    testClearAfterYear(TimeUnit.DECADE, -1, -10);
    testClearAfterYear(TimeUnit.DECADE, 0, 0);

    testClearAfterYear(TimeUnit.CENTURY, 1499, 1400);
    testClearAfterYear(TimeUnit.CENTURY, 2008, 2000);
    testClearAfterYear(TimeUnit.CENTURY, -1, -100);
    testClearAfterYear(TimeUnit.CENTURY, 0, 0);
    
    testClearAfterYear(TimeUnit.MILLENIUM, 1970, 1000);
    testClearAfterYear(TimeUnit.MILLENIUM, -1, -1000);
    testClearAfterYear(TimeUnit.MILLENIUM, 0, 0);
  }
  
  private void testClearAfterYear(TimeUnit timeUnit, int initialYearValue, int expectedYearValue) {
    DateFields df = getTestDateFields();
    df.setYear(initialYearValue);
    df.clearStartingAfter(timeUnit);
    assertEquals(expectedYearValue, df.year);
    assertEquals(0, df.month);
    assertEquals(1, df.day);
    assertEquals(0, df.hour);
    assertEquals(0, df.minute);
    assertEquals(0, df.second);
    assertEquals(0, df.ms);
  }
  
  private static DateFields getTestDateFields() {
    DateFields df = new DateFields();
    df.setYear(1987).setMonth(5).setDay(19);
    df.hour = 10;
    df.minute = 56;
    df.second = 57;
    df.ms = 999;
    
    return df;

  }
  

}
