package org.timepedia.chronoscope.nongwt;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.date.DayOfWeek;
import org.timepedia.chronoscope.client.util.date.EraCalc;
import org.timepedia.chronoscope.client.util.date.GregorianEraCalc;

/**
 * @author chad takahashi
 */
public class GregorianEraCalcTest extends EraCalcTest {
  
  public void testCalcYearTimestamp() {
    GregorianEraCalc eraCalc = (GregorianEraCalc)getEraCalc();
    testCalcYearTimestamp(eraCalc, eraCalc.getMinYear(), eraCalc.getMaxYear());
  }
  
  public void testAssignYearField() {
    GregorianEraCalc eraCalc = (GregorianEraCalc)getEraCalc();
    testAssignYearField(eraCalc, eraCalc.getMinYear(), eraCalc.getMaxYear());
  }
  
  public void testIsLeapYear() {
    GregorianEraCalc eraCalc = (GregorianEraCalc)getEraCalc();
    
    for (int y = eraCalc.getMinYear(); y <= eraCalc.getMaxYear(); y++) {
      boolean expected = isLeapYear(y);
      assertEquals(expected, eraCalc.isLeapYear(y));
    }
  }
  
  public void testCalcDayOfWeek() {
    GregorianEraCalc eraCalc = (GregorianEraCalc)getEraCalc();
    super.testCalcDayOfWeek(eraCalc, eraCalc.getMinYear(), eraCalc.getMaxYear());
  }

  public void testCalcWeekOfYear() {
    GregorianEraCalc eraCalc = (GregorianEraCalc)getEraCalc();
    super.testCalcWeekOfYear(eraCalc);
  }

  // Traditional formula for calculating the day-of-week for an arbitary
  // Gregorian date:
  private static final int[] ZELLAR_MONTHS = {11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
  private static DayOfWeek calcDayOfWeek(int year, int month, int day) {
    // Formula: f = k + [(13*m-1)/5] + D + [D/4] + [C/4] - 2*C

    int k = day;
    int m = ZELLAR_MONTHS[month];
    if (month == 0 || month == 1) {
      // If January of February, need to roll back to prev. year due to Zellar's
      // month convention.
      --year;
    }
    int D = MathUtil.mod(year, 100);
    int C = year / 100;
    int f = k + ((13*m-1)/5) + D + (D/4) + (C/4) - 2*C;
    int dow = MathUtil.mod(f, 7);
    return DayOfWeek.values()[dow];
  }
  
  @Override
  protected boolean isLeapYear(int year) {
    // Gregorian era: every 4th year is a leap year *except* for 
    // century years not divisible by 400
    boolean isDivisibleBy4 = (year & 0x3) == 0;
    if (isDivisibleBy4) {
      boolean isCenturyYear = (year % 100 == 0);
      boolean isDivisibleBy400 = (year % 400 == 0);
      return !(isCenturyYear && !isDivisibleBy400);
    }
    else {
      return false;
    }
  }

  @Override
  protected EraCalc getEraCalc() {
    return new GregorianEraCalc(1583, 2999);
  }

}
