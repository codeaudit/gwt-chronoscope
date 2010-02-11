package org.timepedia.chronoscope.nongwt;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.date.EraCalc;
import org.timepedia.chronoscope.client.util.date.JulianEraCalc;

/**
 * @author chad takahashi
 */
public final class JulianEraCalcTest extends EraCalcTest {

  @Override
  protected EraCalc getEraCalc() {
    return new JulianEraCalc();
  }

  @Override
  protected boolean isLeapYear(int year) {
    return MathUtil.mod(year, 4) == 0;
  }

  public void testAssignYearField() {
    JulianEraCalc eraCalc = new JulianEraCalc();
    testAssignYearField(eraCalc, -15000, 1581);
    
    // Test some really old dates
    int[] yearStarts = 
        {-50000, -100000, -200000, -500000, -600000, -800000, -900000, -1000000, -1500000};
    for (int i : yearStarts) {
      testAssignYearField(eraCalc, i - 500, i + 500);
    }
  }

  public void testCalcDayOfWeek() {
    EraCalc eraCalc = getEraCalc();

    testCalcDayOfWeek(eraCalc, 800, 1581);
    testCalcDayOfWeek(eraCalc, -980, 20);
    testCalcDayOfWeek(eraCalc, -4850, -4730);
    testCalcDayOfWeek(eraCalc, -8000, -7950);
    testCalcDayOfWeek(eraCalc, -1000050, -999950);
  }

  public void testCalcWeekOfYear() {
    EraCalc eraCalc = getEraCalc();

    // TODO - test week of year for Julian 
    // testCalcWeekOfYear(eraCalc);
  }

  public void testCalcYearTimestamp() {
    EraCalc eraCalc = getEraCalc();
    testCalcYearTimestamp(eraCalc, -2000, 1581);
    testCalcYearTimestamp(eraCalc, -10500, -9500);
    testCalcYearTimestamp(eraCalc, -100500, -99500);
    testCalcYearTimestamp(eraCalc, -300000, -299000);
    testCalcYearTimestamp(eraCalc, -900000, -899000);
    testCalcYearTimestamp(eraCalc, -2000000, -1999000);
    //testCalcYearTimestamp(eraCalc, -999999999, -999999998); // java.util.Date breaks?
  }
}
