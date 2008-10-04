package org.timepedia.chronoscope.client.util.date;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.TimeUnit;

import java.util.Date;

/**
 * @author chad takahashi
 */
public class DefaultChronoDateTest extends TestCase {
  
  public void testGetDaysInMonth() {
    ChronoDate d = createDate(new Date(1582-1900, 0, 1).getTime());
    assertEquals(31, d.getDaysInMonth());

    d = createDate(new Date(2000-1900, 1, 15).getTime());
    assertEquals(29, d.getDaysInMonth());
  
    d = createDate(new Date(1900-1900, 1, 15).getTime());
    assertEquals(28, d.getDaysInMonth());

    // Special case: switch from Julian to Gregorian; Oct only has 21 days.
    d = createDate(new Date(1582-1900, 9, 3).getTime());
    assertEquals(21, d.getDaysInMonth());
  }
  
  public void testSetTime() {
    final int expectedYear = 1997;
    final int expectedMonth = 5;
    final int expectedDay = 15;
    final int expectedHour = 14;
    Date testDate = new Date(expectedYear - 1900, expectedMonth, expectedDay, expectedHour, 0, 0);
    
    ChronoDate d = createDate();
    d.setTime(testDate.getTime());
    assertEquals(expectedYear, d.getYear());
    assertEquals(expectedMonth, d.getMonth());
    assertEquals(expectedDay, d.getDay());
    assertEquals(expectedHour, d.getHour());
    // TODO: test for minutes, seconds, ...
  }
  
  public void testAddYears() {
    // Add 11 years to date
    Date testDate = new Date(1900+55, 10, 5, 14, 0, 0);
    ChronoDate d = createDate(testDate.getTime());
    d.add(TimeUnit.YEAR, 11);
    assertEquals((long)d.getTime(), new Date(1900+55 + 11, 10, 5, 14, 0, 0).getTime());
  }
  
  public void testAddDays() {
    int[] startYears = new int[] {1590, 1750, 1900, 1942, 1943, 1945, 1950, 1971, 1990, 2007, 2999};
    
    for (int i = 0; i < startYears.length; i++) {
      for (int j = 0; j < 400; j++) {
        testAddDays(startYears[i], j);
      }
    }
  }
  
  private void testAddDays(int startYear, int numDays) {
    final int startMonth = 1;
    final int startDay = 27;
    
    Date testDate = new Date(startYear-1900, startMonth, startDay);
    ChronoDate d = createDate(testDate.getTime());
    d.add(TimeUnit.DAY, numDays);
    assertEquals((long)d.getTime(), new Date(startYear-1900, startMonth, startDay + numDays).getTime());
  }
  
  protected ChronoDate createDate(double timeStamp) {
    return new DefaultChronoDate(timeStamp);
  }
  
  protected ChronoDate createDate() {
    return createDate(new Date().getTime());
  }
  
}
