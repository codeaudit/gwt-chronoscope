package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.junit.ObjectSmokeTest;
import org.timepedia.chronoscope.client.util.junit.TestObjectFactory;

/**
 * @author chad takahashi
 */
public class IntervalTest extends TestCase {
  private TestObjectFactory intervalFactory;
  
  public IntervalTest() {
    // Create a TestObjectFactory instance that generates Integer objects.
    this.intervalFactory = new TestObjectFactory() {
      public Object getInstance(int index) {
        return new Interval(index + 0.5, index + 10.5);
      }
      public int instanceCount() {
        return 999;
      }
    };
  }
  
  public void testObjectEssentials() {
    ObjectSmokeTest smokeTest = new ObjectSmokeTest(this.intervalFactory);
    smokeTest.testAll();
  }

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
  
  public void testExpand() {
    Interval i = new Interval(1, 3);
    i.expand(-7);
    assertEquals(new Interval(-7, 3), i);

    i = new Interval(1, 3);
    i.expand(5);
    assertEquals(new Interval(1, 5), i);

    i = new Interval(1, 3);
    i.expand(1);
    assertEquals(new Interval(1, 3), i);
    
    
    i = new Interval(1, 3);
    i.expand(2);
    assertEquals(new Interval(1, 3), i);

    i = new Interval(1, 3);
    i.expand(3);
    assertEquals(new Interval(1, 3), i);

  }
  
  public void testExpandInterval() {
    Interval i = new Interval(1, 3);
    i.expand(new Interval(-2, 2));
    assertEquals(new Interval(-2, 3), i);
    
    i = new Interval(1, 3);
    i.expand(new Interval(1.5, 2.5));
    assertEquals(new Interval(1, 3), i);
    
    i = new Interval(1, 3);
    i.expand(new Interval(0, 4));
    assertEquals(new Interval(0, 4), i);
  }
  
  public void testGetEnd() {
    assertEquals(10.0, new Interval(5, 10).getEnd());
    assertEquals(-10.0, new Interval(-12, -10).getEnd());
  }
  
  public void testGetStart() {
    assertEquals(5.0, new Interval(5, 10).getStart());
    assertEquals(-5.0, new Interval(-5, 10).getStart());
  }
  
  public void testIntersects() {
    Interval i = new Interval(-4, 4);
    assertTrue(i.intersects(new Interval(-5, -4)));
    assertTrue(i.intersects(new Interval(-5, 3)));
    assertTrue(i.intersects(new Interval(-5, 4)));
    assertTrue(i.intersects(new Interval(-5, 5)));
    assertTrue(i.intersects(new Interval(-3, -2)));
    assertTrue(i.intersects(new Interval(-3, 4)));
    assertTrue(i.intersects(new Interval(-3, 4)));
    assertTrue(i.intersects(new Interval(-3, 5)));
    assertTrue(i.intersects(new Interval(2, 5)));
    assertTrue(i.intersects(new Interval(4, 4)));
    assertTrue(i.intersects(new Interval(4, 5)));
    assertTrue(i.intersects(i));
    
    assertFalse(i.intersects(new Interval(-7, -6)));
    assertFalse(i.intersects(new Interval(4.01, 4.02)));
    assertFalse(i.intersects(new Interval(5, 7)));
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
  
  public void testGetPointFromRatio() {
    assertEquals(15.0, new Interval(10, 20).getPointFromRatio(0.5));

    assertEquals(10.0, new Interval(10, 10).getPointFromRatio(0.0));
    assertEquals(10.0, new Interval(10, 10).getPointFromRatio(0.4));
    assertEquals(10.0, new Interval(10, 10).getPointFromRatio(1.0));
  }
  
  public void testGetRatioFromPoint() {
    assertEquals(0.5, new Interval(10, 20).getRatioFromPoint(15));
    assertEquals(0.0, new Interval(10, 20).getRatioFromPoint(10));
    assertEquals(1.0, new Interval(10, 20).getRatioFromPoint(20));

    assertEquals(0.0, new Interval(10, 10).getRatioFromPoint(15));
 }
  
  public void testSetEndpoints() {
    Interval i = new Interval(10, 20);
    i.setEndpoints(-5, 5);
    assertEquals(-5.0, i.getStart());
    assertEquals(5.0, i.getEnd());
  }
}
