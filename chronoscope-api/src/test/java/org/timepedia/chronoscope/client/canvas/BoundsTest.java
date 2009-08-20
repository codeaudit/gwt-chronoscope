/**
 * 
 */
package org.timepedia.chronoscope.client.canvas;

import junit.framework.TestCase;

/**
 * @author Chad Takahashi
 */
public class BoundsTest extends TestCase {


  public void testConstructor() {
    Bounds b = new Bounds();
    assertEquals(0.0, b.x);
    assertEquals(0.0, b.y);
    assertEquals(0.0, b.height);
    assertEquals(0.0, b.width);
  }

  public void testArea() {
    assertEquals(15.0, new Bounds(0, 0, 3, 5).area());
    assertEquals(15.0, new Bounds(99, 99, 3, 5).area());
    assertEquals(0.0, new Bounds(99, 99, 0, 0).area());
  }

  public void testCopyTo() {
    Bounds src = new Bounds(1, 2, 3, 4);
    Bounds tgt = new Bounds();
    src.copyTo(tgt);
    assertEquals(src.x, tgt.x);
    assertEquals(src.y, tgt.y);
    assertEquals(src.width, tgt.width);
    assertEquals(src.height, tgt.height);
  }
  
  public void testInside() {
    Bounds b = new Bounds(10, 20, 2, 5);
    
    assertTrue(b.inside(10 + 1, 20 + 3));

    // test some perimeter points (which are considered "inside")
    assertTrue(b.inside(10, 20));
    assertTrue(b.inside(10 + 2, 20));
    assertTrue(b.inside(10 + 2, 20 + 5));
    assertTrue(b.inside(10, 20 + 5));
    
    // these points are outside of bounding box, and should fail
    assertFalse(b.inside(10 - 1, 20));
    assertFalse(b.inside(10, 20 - 1));
  }
  
  public void testCopyConstructor() {
    Bounds copy = new Bounds(new Bounds(1,2,3,4));
    assertEquals(1.0, copy.x);
    assertEquals(2.0, copy.y);
    assertEquals(3.0, copy.width);
    assertEquals(4.0, copy.height);
  }
  
  public void testBottomY() {
    Bounds b = new Bounds(10, 15, 100, 200);
    assertEquals(b.y + b.height, b.bottomY());
  }
  
  public void testMidpointX() {
    assertEquals(2.0 + (10.0 / 2.0), new Bounds(2, 3, 10, 20).midpointX());
  }

  public void testRightX() {
    Bounds b = new Bounds(10, 15, 100, 200);
    assertEquals(b.x + b.width, b.rightX());
  }
  
  public void testSetPosition() {
    Bounds b = new Bounds();
    b.setPosition(2.0, 3.0);
    assertEquals(2.0, b.x);
    assertEquals(3.0, b.y);
  }
}
