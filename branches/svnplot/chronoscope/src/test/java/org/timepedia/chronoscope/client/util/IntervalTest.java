package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author chad takahashi
 */
public class IntervalTest extends TestCase {
  
  public void testConstructor() {
    Interval i = new Interval(-2, 2);
    assertEquals(-2.0, i.getStart());
    assertEquals(2.0, i.getEnd());
  }
  
  public void testContains() {
    Interval i = new Interval(2, 3);
    assertFalse(i.contains(1.9));
    assertTrue(i.contains(2.0));
    assertTrue(i.contains(2.5));
    assertTrue(i.contains(3.0));
    assertFalse(i.contains(3.1));
  }
  
  public void testContainsOpen() {
    Interval i = new Interval(2, 3);
    assertFalse(i.containsOpen(1.9));
    assertFalse(i.containsOpen(2.0));
    assertTrue(i.containsOpen(2.5));
    assertFalse(i.containsOpen(3.0));
    assertFalse(i.containsOpen(3.1));
  }
  public void testCopy() {
    Interval i = new Interval(1, 2);
    Interval lsCopy = i.copy();
    assertTrue(i != lsCopy);
    assertEquals(i.getStart(), lsCopy.getStart());
    assertEquals(i.getEnd(), lsCopy.getEnd());
  }
  
  public void testCopyTo() {
    Interval i = new Interval(1, 2);
    Interval lsCopy = new Interval(99, 99);
    i.copyTo(lsCopy);
    assertEquals(i.getStart(), lsCopy.getStart());
    assertEquals(i.getEnd(), lsCopy.getEnd());
  }
  
  public void testGetEnd() {
    assertEquals(10.0, new Interval(5, 10).getEnd());
    assertEquals(-10.0, new Interval(-12, -10).getEnd());
  }
  
  public void testGetStart() {
    assertEquals(5.0, new Interval(5, 10).getStart());
    assertEquals(-5.0, new Interval(-5, 10).getStart());
  }
  
  public void testLength() {
    assertEquals(0.0, new Interval(5, 5).length());
    assertEquals(1.0, new Interval(5, 6).length());
    assertEquals(1.0, new Interval(-5, -4).length());
    assertEquals(100.0, new Interval(-50, 50).length());
  }
  
  public void testMidpoint() {
    assertEquals(2.0, new Interval(1, 3).midpoint());
    assertEquals(-2.0, new Interval(-3, -1).midpoint());
    assertEquals(0.0, new Interval(-3, 3).midpoint());
  }
  
  public void testSetEndpoints() {
    Interval i = new Interval(10, 20);
    i.setEndpoints(-5, 5);
    assertEquals(-5.0, i.getStart());
    assertEquals(5.0, i.getEnd());
  }
}
