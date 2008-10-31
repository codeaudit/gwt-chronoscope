package org.timepedia.chronoscope.client.util.date;

import junit.framework.TestCase;

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
  
  private static DateFields getTestDateFields() {
    DateFields df = new DateFields();
    df.setYear(1980).setMonth(5).setDay(19);
    df.hour = 10;
    df.minute = 56;
    df.second = 57;
    df.ms = 999;
    
    return df;

  }
}
