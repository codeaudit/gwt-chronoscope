package org.timepedia.chronoscope.client.render;

import junit.framework.TestCase;

import java.util.Date;
import java.util.Iterator;

/**
 * @author Chad Takahashi
 *
 */
public class ZoomIntervalsTest extends TestCase {
  private ZoomInterval z1d, z1m, z1y;
  
  public ZoomIntervalsTest(String name) {
    super(name);
  }
  
  public void setUp() {
    z1d = new ZoomInterval("1d", 1);
    z1m = new ZoomInterval("1m", 30);
    z1y = new ZoomInterval("1y", 365);
  }
  
  public void testConstructor() {
    ZoomIntervals z = new ZoomIntervals();
    assertEquals(0, calcSize(z));
  }

  public void testAdd() {
    ZoomIntervals z = new ZoomIntervals();
    
    // Add ZoomIntervals out of order to test sorting
    z.add(z1m);
    z.add(z1y);
    z.add(z1d);
    z.add(z1m); // verify that dupes get removed
    
    // Verify that elements are iterated in sorted order.
    Iterator<ZoomInterval> itr = z.iterator();
    assertEquals(z1d, itr.next());
    assertEquals(z1m, itr.next());
    assertEquals(z1y, itr.next());
    assertFalse("Unexpected element in iterator", itr.hasNext());
  }
  
  public void testFilter() {
    ZoomIntervals z = new ZoomIntervals();
    z.add(z1d);
    z.add(z1m);
    z.add(z1y);
    
    // Pick a start/end time whose interval is less than a year.
    // We expect the year zoom to drop out.
    double timeStart = new Date().getTime();
    double timeEnd = timeStart + z1y.getInterval() - 1;
    z.applyFilter(timeStart, timeEnd, 0);
    assertZoomEquals(z.iterator(), z1d, z1m);
    
    // Now clear the filter and make sure it goes back to initial state
    z.clearFilter();
    assertZoomEquals(z.iterator(), z1d, z1m, z1y);
    
    // TODO: add more filter test cases
    
  }
  
  private static int calcSize(Iterable<?> i) {
    int size = 0;
    for (Object obj : i) {
      ++size;
    }
    return size;
  }
  
  private static void assertZoomEquals(Iterator<ZoomInterval> actual, ZoomInterval... expected) {
    for (int i = 0; i < expected.length; i++) {
      TestCase.assertEquals(expected[i], actual.next());
    }
    assertFalse(actual.hasNext());
  }
}
