/**
 * 
 */
package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author chad takahashi
 */
public class SimpleDateTest extends TestCase {
  
  public SimpleDateTest(String name) {
    super(name);
  }
  
  public void testGetYear() {
    for (int y = 1200; y < 2100; y++) {
      double t = new GregorianCalendar(y, 1, 1).getTimeInMillis();
      assertEquals(y, SimpleDate.get(t).getYear());
    }
  }
  
  public void testGetMonth() {
    for (int y = 1200; y < 2100; y++) {
      for (int m = 0; m < 12; m++) {
        double t = new GregorianCalendar(y, m, 1).getTimeInMillis();
        assertEquals(m, SimpleDate.get(t).getMonth());
      }
    }
  }

  public void testGetDayOfMonth() {
    assertEquals(1, SimpleDate.get(2008, 0, 1).getDayOfMonth());
    assertEquals(15, SimpleDate.get(2008, 0, 15).getDayOfMonth());
    assertEquals(31, SimpleDate.get(2008, 0, 31).getDayOfMonth());
    assertEquals(31, SimpleDate.get(2008, 11, 31).getDayOfMonth());

    assertEquals(28, SimpleDate.get(2008, 1, 28).getDayOfMonth());
    assertEquals(29, SimpleDate.get(2008, 1, 29).getDayOfMonth());
  }
  
  public void testGetHoursMinutesSeconds() {
    Calendar c = new GregorianCalendar();
    c.set(Calendar.HOUR_OF_DAY, 14);
    c.set(Calendar.MINUTE, 55);
    c.set(Calendar.SECOND, 27);
    
    SimpleDate d = SimpleDate.get((double)c.getTimeInMillis());
    assertEquals(14, d.getHours());
    assertEquals(55, d.getMinutes());
    assertEquals(27, d.getSeconds());
  }
  
}
