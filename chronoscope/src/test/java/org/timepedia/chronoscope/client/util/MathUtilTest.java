/**
 * 
 */
package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author Chad Takahashi &lt;chad@timepedia.org&gt;
 */
public class MathUtilTest extends TestCase {

  public void testBound_double() {
    assertEquals(1.0, MathUtil.bound(1.0, -2.0, 2.0));
    assertEquals(1.0, MathUtil.bound(1.0, 1.0, 2.0));
    assertEquals(1.0, MathUtil.bound(1.0, -1.0, 1.0));
    assertEquals(1.0, MathUtil.bound(-5.0, 1.0, 5.0));
    assertEquals(1.0, MathUtil.bound(9.0, -1.0, 1.0));
  }
  
  public void testBound_int() {
    assertEquals(1, MathUtil.bound(1, -2, 2));
    assertEquals(1, MathUtil.bound(1, 1, 2));
    assertEquals(1, MathUtil.bound(1, -1, 1));
    assertEquals(1, MathUtil.bound(-5, 1, 5));
    assertEquals(1, MathUtil.bound(9, -1, 1));
  }
  public void testIsBounded() {
    assertTrue(MathUtil.isBounded(1, 1, 10));
    assertTrue(MathUtil.isBounded(5, 1, 10));
    assertTrue(MathUtil.isBounded(10, 1, 10));
    assertFalse(MathUtil.isBounded(0, 1, 10));
    assertFalse(MathUtil.isBounded(11, 1, 10));

    assertTrue(MathUtil.isBounded(1.0, 1.0, 10.0));
    assertTrue(MathUtil.isBounded(5.0, 1.0, 10.0));
    assertTrue(MathUtil.isBounded(10.0, 1.0, 10.0));
    assertFalse(MathUtil.isBounded(0.0, 1.0, 10.0));
    assertFalse(MathUtil.isBounded(11.0, 1.0, 10.0));
  }
  
  public void testFindSmallestInterval() {
    assertEquals(0.0, MathUtil.findSmallestInterval(new double[] {10, 20, 20, 30}));
    assertEquals(2.0, MathUtil.findSmallestInterval(new double[] {10, 12}));
    assertEquals(2.0, MathUtil.findSmallestInterval(new double[] {10, 1000, 1002, 1004}));
    assertEquals(1.0, MathUtil.findSmallestInterval(new double[] { 4, 3, 2, 1}));

    try {
      MathUtil.findSmallestInterval(new double[] { 42 });
      fail("Expected IllegalArgumentException since array has only 1 element");
    }
    catch (IllegalArgumentException e) {}

  }
  
  public void testLog2() {
    
    // Typical cases
    assertEquals(3.0, MathUtil.log2(8));
    assertEquals(0.0, MathUtil.log2(1));
    
    // Corner cases
    assertEquals(Double.NEGATIVE_INFINITY, MathUtil.log2(-0));
    assertEquals(Double.NEGATIVE_INFINITY, MathUtil.log2(0));
    assertEquals(Double.NaN, MathUtil.log2(-1));
    assertEquals(Double.NaN, MathUtil.log2(Double.NaN));
  }
  
  public void testMax() {
    assertEquals(2.0, MathUtil.max(2, 1));
    assertEquals(2.0, MathUtil.max(1, 2));
    assertEquals(2.0, MathUtil.max(2, 2));
    assertEquals(2.0, MathUtil.max(2, -2));
    assertEquals(-2.0, MathUtil.max(-2, -3));
  }
  
  public void testMin() {
    assertEquals(1.0, MathUtil.min(2, 1));
    assertEquals(1.0, MathUtil.min(1, 2));
    assertEquals(1.0, MathUtil.min(1, 1));
    assertEquals(-2.0, MathUtil.min(2, -2));
    assertEquals(-3.0, MathUtil.min(-2, -3));
  }
  
  public void testMod() {
    // negative values
    assertEquals(2, MathUtil.mod(-4, 3));
    assertEquals(0, MathUtil.mod(-3, 3));
    assertEquals(1, MathUtil.mod(-2, 3));
    assertEquals(2, MathUtil.mod(-1, 3));
    // positive values
    assertEquals(0, MathUtil.mod(0, 3));
    assertEquals(1, MathUtil.mod(1, 3));
    assertEquals(2, MathUtil.mod(2, 3));
    assertEquals(0, MathUtil.mod(3, 3));
    assertEquals(1, MathUtil.mod(4, 3));
  }
}
