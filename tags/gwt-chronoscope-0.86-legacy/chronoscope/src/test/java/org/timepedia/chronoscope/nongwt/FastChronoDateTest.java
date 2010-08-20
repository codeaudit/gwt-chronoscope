package org.timepedia.chronoscope.nongwt;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DayOfWeek;
import org.timepedia.chronoscope.client.util.date.FastChronoDate;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author chad takahashi
 */
public class FastChronoDateTest extends TestCase {
  private TimeZone origTimeZone;
  
  public void setUp() {
    origTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    //System.out.println("setUp(): TZINFO: " + TimeZone.getDefault().getDisplayName());
  }
  
  public void tearDown() {
    TimeZone.setDefault(origTimeZone);
    //System.out.println("tearDown(): TZINFO: " + TimeZone.getDefault().getDisplayName());
  }
    
  public void testAgainstJavaDate() {
    // Test some really old prehistoric dates
    int[] prehistoricYears = new int[] {
        -36531904, -32000000, -20000000, -10000000, -9999999, -5000001, -999999,
        -750001, -650000, -500000, -429000, -305983, -264123, -153984, -99999,
        -59821, -50444, -23000, -15000, -9842, -7084, -5984, -3291, -2004
        };
    for (int i = 0; i < prehistoricYears.length ;i++) {
      testAgainstJavaDate(prehistoricYears[i]);
    }
    
    // Test up to end of Julian era
    for (int y = -1000; y < 1582; y++) {
      testAgainstJavaDate(y);
    }
    
    // Test special case: Julian-to-Gregorian transition year
    //testAgainstJavaDate(1582);

    // Test Gregorian era
    for (int y = 1583; y < 3000; y++) {
      testAgainstJavaDate(y);
    }

  }
  
  private void testAgainstJavaDate(int year) {
    final String msg = "year=" + year + ": ";
    
    int hr = 23;
    int min = 17;
    int sec = 50;
    
    Date javaDate = createJavaDate(year, 0, 1, hr, min, sec);
    ChronoDate chronoDate = createTestDate(year, 0, 1, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());
    
    javaDate = createJavaDate(year, 0, 31, hr, min, sec);
    chronoDate = createTestDate(year, 0, 31, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());

    javaDate = createJavaDate(year, 1, 28, hr, min, sec);
    chronoDate = createTestDate(year, 1, 28, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());
    
    javaDate = createJavaDate(year, 2, 1, hr, min, sec);
    chronoDate = createTestDate(year, 2, 1, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());

    javaDate = createJavaDate(year, 5, 15, hr, min, sec);
    chronoDate = createTestDate(year, 5, 15, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());
    
    javaDate = createJavaDate(year, 9, 1, hr, min, sec);
    chronoDate = createTestDate(year, 9, 1, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());
    
    javaDate = createJavaDate(year, 9, 30, hr, min, sec);
    chronoDate = createTestDate(year, 9, 30, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());

    javaDate = createJavaDate(year, 11, 31, hr, min, sec);
    chronoDate = createTestDate(year, 11, 31, hr, min, sec, 0);
    assertTimestampEquals(msg, year, javaDate.getTime(), (long)chronoDate.getTime());
  }
  
  public void testMillisecondPrecision() {
    // test precision using add()
    {
      ChronoDate d = ChronoDate.get(1980, 0, 1);
      double expectedTs = d.getTime();
      for (int i = 0; i < 10000; i++) {
        d.add(TimeUnit.MS, 1);
        expectedTs += 1.0;
        assertEquals(expectedTs, d.getTime());
      }
    }
    
    // test precision using setTime()
    {
      ChronoDate d = ChronoDate.get(1980, 0, 1);
      double expectedTs = d.getTime();
      for (int i = 0; i < 1000; i++) {
        ++expectedTs;
        d.setTime(expectedTs);
        assertEquals(expectedTs, d.getTime());
      }
    }
  }
  
  public void testSetTime() {
    final int year = 1255;
    final int month = 4;
    final int day = 15;
    final int hour = 20;
    final int minute = 1;
    final int second = 59;
    final double ts = createJavaDate(year, month, day, hour, minute, second).getTime();
    
    // Set the time in milliseconds and verify that it's correctly translated into
    // the constituent date fields.
    ChronoDate d = createTestDate(1580);
    d.setTime(ts);
    assertEquals(year, month, day, hour, minute, second, d);
  }
  
  /**
   * Tests the set() and getTime() methods.
   */
  public void testSetTimeUnitFields() {
    testSetTimeUnitFields(2999, 11, 5, 23, 12, 59);
    testSetTimeUnitFields(2500, 7, 5, 23, 12, 59);
    testSetTimeUnitFields(2413, 6, 5, 23, 12, 50);
    testSetTimeUnitFields(2400, 6, 5, 23, 12, 0);
    testSetTimeUnitFields(2100, 6, 5, 23, 12, 13);
    testSetTimeUnitFields(2000, 5, 5, 23, 12, 59);
    testSetTimeUnitFields(1970, 4, 1, 0, 0, 0);
    testSetTimeUnitFields(1950, 2, 5, 23, 12, 59);
    testSetTimeUnitFields(1950, 0, 1, 0, 0, 0);

    testSetTimeUnitFields(1574, 2, 5, 23, 12, 59);
    testSetTimeUnitFields(0, 11, 15, 15, 50, 23);
    testSetTimeUnitFields(-5000, 11, 15, 15, 50, 23);
    testSetTimeUnitFields(-999999, 4, 2, 20, 1, 23);
  }
  
  public void testAddYears() {
    ChronoDate d = createTestDate(1195, 2, 15); // non-leapyear
    d.add(TimeUnit.YEAR, 300);
    assertEquals(1495, 2, 15, d);

    d = createTestDate(-750000, 5, 10);
    d.add(TimeUnit.YEAR, 100000);
    assertEquals(-650000, 5, 10, d);

    d = createTestDate(1575, 5, 10);
    d.add(TimeUnit.YEAR, 7);
    assertEquals(1582, 5, 10, d);

    d = createTestDate(1580, 5, 10);
    d.add(TimeUnit.YEAR, 4);
    assertEquals(1584, 5, 10, d);

    d = createTestDate(-100, 5, 10);
    d.add(TimeUnit.YEAR, 2600);
    assertEquals(2500, 5, 10, d);

    d = createTestDate(1901, 5, 10);
    d.add(TimeUnit.YEAR, 44);
    assertEquals(1945, 5, 10, d);

    d = createTestDate(1955, 5, 10);
    d.add(TimeUnit.YEAR, 100);
    assertEquals(2055, 5, 10, d);
  }

  public void testAddMonths() {
    ChronoDate d = createTestDate(1576, 3, 15);
    d.add(TimeUnit.MONTH, 7);
    assertEquals(1576, 10, 15, d);
    
    d = createTestDate(1576, 3, 15);
    d.add(TimeUnit.MONTH, 15);
    assertEquals(1577, 6, 15, d);
    
    d = createTestDate(1580, 3, 15);
    d.add(TimeUnit.MONTH, 26);
    assertEquals(1582,5, 15, d);

    d = createTestDate(1580, 3, 15);
    d.add(TimeUnit.MONTH, 12*5+5);
    assertEquals(1585,8, 15, d);

    d = createTestDate(-100000, 11, 31);
    d.add(TimeUnit.MONTH, 5);
    assertEquals(-99999, 4, 31, d);
  }

  public void testAddDays() {
    ChronoDate d = createTestDate(1575, 1, 28); // non-leapyear
    d.add(TimeUnit.DAY, 2);
    assertEquals(1575, 2, 2, d);

    d = createTestDate(1576, 1, 28); // leapyear
    d.add(TimeUnit.DAY, 2);
    assertEquals(1576, 2, 1, d);

    d = createTestDate(-3, 11, 25); // non-leapyear
    d.add(TimeUnit.DAY, 10);
    assertEquals(-2, 0, 4, d);

    d = createTestDate(1581, 11, 25);
    d.add(TimeUnit.DAY, 10);
    assertEquals(1582, 0, 4, d);

    d = createTestDate(1582, 9, 1);
    d.add(TimeUnit.DAY, 15);
    assertEquals(1582, 9, 16, d);

    d = createTestDate(1582, 11, 25);
    d.add(TimeUnit.DAY, 10);
    assertEquals(1583, 0, 4, d);
    
    // Test that day overflow logic works:
    d = createTestDate(1975, 0, 31); // non-leapyear
    d.add(TimeUnit.DAY, 35);
    assertEquals(1975, 2, 7, d);
    d = createTestDate(1976, 0, 31); // leapyear
    d.add(TimeUnit.DAY, 35);
    assertEquals(1976, 2, 6, d);
    d = createTestDate(1976, 0, 1); // skip an entire year (leapyear)
    d.add(TimeUnit.DAY, 366);
    assertEquals(1977, 0, 1, d);
  }
  
  public void testAddWeeks() {
    ChronoDate d = createTestDate(2008, 1, 27); // leapyear
    d.add(TimeUnit.WEEK, 2);
    assertEquals(2008, 2, 12, d);

    d = createTestDate(2008, 1, 27); // leapyear
    d.add(TimeUnit.WEEK, 4);
    assertEquals(2008, 2, 26, d);

    // This test hits the Julian, JulianGregorianCrossover, and Gregorian EraCalc
    // subclasses.  Verifies that the day-of-week value is the same for each week
    // that we increment.
    d = createTestDate(1581, 0, 15);
    DayOfWeek dow = d.getDayOfWeek();
    for (int i = 0; i < (52 * 3); i++) {
      d.add(TimeUnit.WEEK, 1);
      assertEquals(dow, d.getDayOfWeek());
    }
  }

  public void testAdd_DECADE() {
    int year = 1892;
    ChronoDate d = ChronoDate.get(year, 1, 1);
    
    while (year < 4999) {
      d.add(TimeUnit.DECADE, 1);
      assertEquals(year + 10, d.getYear());
      year += 10;
    }
    
  }
  
  public void testAdd_CENTURY() {
    int year = -5124;
    ChronoDate d = ChronoDate.get(year, 1, 1);
    
    while (year < 4999) {
      d.add(TimeUnit.CENTURY, 1);
      assertEquals(year + 100, d.getYear());
      year += 100;
    }
    
  }

  public void testTruncate() {
    ChronoDate d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.YEAR);
    assertEquals(1492, 0, 1, d);
    
    d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.MONTH);
    assertEquals(1492, 4, 1, d);

    d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.DAY);
    assertEquals(1492, 4, 21, d);

    d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.HOUR);
    assertEquals(1492, 4, 21, 23, 0, 0, d);

    d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.MIN);
    assertEquals(1492, 4, 21, 23, 56, 0, d);

    d = createTestDate(1492, 4, 21, 23, 56, 31, 555);
    d.truncate(TimeUnit.SEC);
    assertEquals(1492, 4, 21, 23, 56, 31, d);
  }
  
  /**
   * Week truncation requires special logic.  Test this functionality separately.
   */
  public void testTruncateWeek() {
    // Monday, Oct 13
    ChronoDate d = createTestDate(2008, 9, 13, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(2008, 9, 12, d);

    // Saturday, Oct 18
    d = createTestDate(2008, 9, 18, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(2008, 9, 12, d);

    // Friday, Oct 3
    d = createTestDate(2008, 9, 3, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(2008, 8, 28, d); // expect Sun, Sept 28 '08

    // Wed, Jan 2, 2008
    d = createTestDate(2008, 0, 2, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(2007, 11, 30, d); // expect Sun, Dec 30, '07

    // Sun, Jan 2, 1200
    d = createTestDate(1200, 0, 2, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(1200, 0, 2, d); // expect Sun, Jan 2, 1200

    // Sat, Jan 1, 1200
    d = createTestDate(1200, 0, 1, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(1199, 11, 26, d); // expect Sun, Dec 26, 1199

    // Tue, Mar 20, 1582
    d = createTestDate(1582, 2, 20, 23, 59, 59, 123);
    d.truncate(TimeUnit.WEEK);
    assertEquals(1582, 2, 18, d);
  }
  
  public void testCopyTo() {
    ChronoDate source = createTestDate(1971, 5, 1);
    source.set().hour(9).min(57).sec(23).ms(555);
    double sourceTimeStamp = source.getTime();
    
    ChronoDate target = ChronoDate.getSystemDate();
    source.copyTo(target);
    assertEquals(sourceTimeStamp, target.getTime());
    
    try {
      ChronoDate.getSystemDate().copyTo(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {}
  }
  
  public void testGetDaysInMonth() {
    ChronoDate d = createTestDate(1582, 0, 1);
    assertEquals(31, d.getDaysInMonth());

    d = createTestDate(2000, 1, 15);
    assertEquals(29, d.getDaysInMonth());
  
    d = createTestDate(1900, 1, 15);
    assertEquals(28, d.getDaysInMonth());

    // Special case: switch from Julian to Gregorian; Oct only has 21 days.
    d = createTestDate(1582, 9, 3);
    assertEquals(21, d.getDaysInMonth());
  }
  
  private void assertEquals(int year, int month, int day, ChronoDate d) {
    assertEquals(year, month, day, 0, 0, 0, d);
  }
  
  private void assertEquals(int year, int month, int day, int hr, int min, int sec, ChronoDate d) {
    assertEquals("incorrect year:", year, d.get(TimeUnit.YEAR));
    assertEquals("incorrect month:", month, d.get(TimeUnit.MONTH));
    assertEquals("incorrect day:", day, d.get(TimeUnit.DAY));
    assertEquals("incorrect hour:", hr, d.get(TimeUnit.HOUR));
    assertEquals("incorrect minute:", min, d.get(TimeUnit.MIN));
    assertEquals("incorrect second:", sec, d.get(TimeUnit.SEC));
    //assertEquals(0, d.get(TimeUnit.MS));
  }
  
  private void assertTimestampEquals(String msg, int year, long expectedTS, long ts) {
    final long diff = Math.abs(expectedTS - ts);
    
    if (year > -200000) {
      if (diff != 0) {
        fail(msg + "diff=" + diff + "; expectedTS=" + expectedTS + "; ts=" + ts);
      }
    }
    else {
      // when the timestamp starts getting this small, the java double can 
      // presumably no longer represent millisecond precision.
      if (diff > 1000) {
        fail(msg + "expectedTS-ts=" + diff + ", which is too large of a delta");
      }
    }
  }
  
  private ChronoDate createTestDateFromMs(double ms) {
    return new FastChronoDate(ms);
  }
  
  private ChronoDate createTestDate(int year) {
    return createTestDate(year, 0, 1, 0, 0, 0, 0);
  }
  
  private static ChronoDate createTestDate(int year, int month, int day) {
    return createTestDate(year, month, day, 0, 0, 0, 0);
  }
  
  private static ChronoDate createTestDate(int year, int month, int day, int hr, int min, int sec, int ms) {
    ChronoDate d = new FastChronoDate(year, month, day);
    d.set().hour(hr).min(min).sec(sec).ms(ms).done();
    return d;
  }
  
  private static Date createJavaDate(int yr, int mo, int day, int hr, int min, int sec) {
    return new Date(yr - 1900, mo, day, hr, min, sec);
  }

  private void testSetTimeUnitFields(int yr, int mo, int day, int hr, int min, int sec) {
    ChronoDate d = createTestDate(yr);
    d.set().month(mo).day(day).hour(hr).min(min).sec(sec).done();
    
    long expectedTimestamp = createJavaDate(yr, mo, day, hr, min, sec).getTime();
    assertEquals(expectedTimestamp, (long)d.getTime());
  }
  
  public void testSetMillenium() {
    ChronoDate d = new FastChronoDate(-2345, 5, 25);
    d.set(TimeUnit.MILLENIUM, 1);
    assertEquals(1345, 5, 25, d);
    
    d = new FastChronoDate(-2345, 5, 25);
    d.set(TimeUnit.MILLENIUM, -30);
    assertEquals(-30345, 5, 25, d);
  }
}
