package org.timepedia.chronoscope.client.util.date;

import junit.framework.TestCase;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author chad takahashi
 */
public abstract class EraCalcTest extends TestCase {
  
  public EraCalcTest(String name) {
    super(name);
  }
  
  protected abstract EraCalc getEraCalc();
  
  protected abstract boolean isLeapYear(int year);
  
  /**
   * Verify that the year timestamp calculated by this EraCalc object exactly matches
   * the corresponding calculation by java.util.Date.
   */
  static final void testCalcYearTimestamp(EraCalc eraCalc, int minYear, int maxYear) {
    for (int y = minYear; y <= maxYear; y++) {
      double eraCalcTime = eraCalc.calcYearTimestamp(y);
      assertEquals("y=" + y, javaDate(y).getTime(), (long)eraCalcTime);
    }
  }
  
  static void testCalcDayOfWeek(EraCalc eraCalc, int minYear, int maxYear) {
    Date javaDate = new Date(0L);
    
    for (int y = minYear; y <= maxYear; y++) {
      boolean isLeapYear = eraCalc.isLeapYear(y);
      for (int m = 0; m < 12; m++) {
        for (int d = 1; d <= eraCalc.getDaysInMonth(m, isLeapYear); d++) {
          DayOfWeek dow = eraCalc.calcDayOfWeek(y, m, d);
          //Date javaDate = javaDate(y, m, d);
          javaDate.setYear(y - 1900);
          javaDate.setMonth(m);
          javaDate.setDate(d);
          DayOfWeek expectedDOW = DayOfWeek.values()[javaDate.getDay()];
          //System.out.println("TESTING: javaDate=" + javaDate + "; dow=" + dow);
          assertEquals("y=" + y + ";m=" + m + ";d=" + d + ": ", expectedDOW, dow);
        }
      }
    }
  }
  
  static void testAssignYearField(EraCalc eraCalc, int minYear, int maxYear) {
    DateFields dateFields = new DateFields();
    
    for (int y = minYear; y <= maxYear; y++) {
      // Case 1: Beginning of year.  Offset should be 0 ms.
      double ts = javaDate(y, 0, 1).getTime();
      double yearOffsetInMs = eraCalc.calcYearField(ts, dateFields);
      long expectedYearOffset = 0L;
      assertEquals("y=" + y, y, dateFields.year);
      assertEquals("y=" + y, expectedYearOffset, (long)yearOffsetInMs);

      // Case 2: beginning of year + 3 minutes and 41 seconds
      ts = javaDate(y, 0, 1, 0, 3, 41).getTime();
      yearOffsetInMs = eraCalc.calcYearField(ts, dateFields);
      expectedYearOffset = (long)((60 * 3 + 41) * 1000);
      assertEquals("y=" + y, y, dateFields.year);
      assertEquals("y=" + y, expectedYearOffset, (long)yearOffsetInMs);

      // Case 2: beginning of year + 4hr, 3min and 41sec
      ts = javaDate(y, 0, 1, 4, 3, 41).getTime();
      yearOffsetInMs = eraCalc.calcYearField(ts, dateFields);
      expectedYearOffset = (long)(((60 * 60 * 4) + (60 * 3) + 41) * 1000);
      assertEquals("y=" + y, y, dateFields.year);
      
      assertEquals("y=" + y, expectedYearOffset, (long)yearOffsetInMs);

    }
  }

  static Date javaDate(int year) {
    return javaDate(year, 0, 1, 0, 0, 0);
  }
  
  static Date javaDate(int year, int month, int day) {
    return javaDate(year, month, day, 0, 0, 0);
  }

  static Date javaDate(int yr, int mo, int day, int hr, int min, int sec) {
    return new Date(yr - 1900, mo, day, hr, min, sec);
  }


}
