package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author chad takahashi
 */
public class TimeUnitTest extends TestCase {

  public void testNextLargest() {
    for (int i = 0; i < TimeUnit.values().length - 1; i++) {
      assertEquals(TimeUnit.values()[i+1],TimeUnit.values()[i].nextLargest());
    }

    assertNull(TimeUnit.values()[TimeUnit.values().length - 1].nextLargest());
  }

  public void testNextSmallest() {
    for (int i = 1; i < TimeUnit.values().length; i++) {
      assertEquals(TimeUnit.values()[i - 1],TimeUnit.values()[i].nextSmallest());
    }
    
    assertNull(TimeUnit.values()[0].nextSmallest());
  }
  
  /**
   * Test commonly-used time units manually.
   */
  public void testSanityCheck() {
    assertEquals(TimeUnit.MIN, TimeUnit.HOUR.nextSmallest());
    assertEquals(TimeUnit.HOUR, TimeUnit.DAY.nextSmallest());
    assertEquals(TimeUnit.DAY, TimeUnit.WEEK.nextSmallest());
    assertEquals(TimeUnit.MONTH, TimeUnit.YEAR.nextSmallest());

    assertEquals(TimeUnit.HOUR, TimeUnit.MIN.nextLargest());
    assertEquals(TimeUnit.DAY, TimeUnit.HOUR.nextLargest());
    assertEquals(TimeUnit.WEEK, TimeUnit.DAY.nextLargest());
    assertEquals(TimeUnit.MONTH, TimeUnit.WEEK.nextLargest());

  }
}
