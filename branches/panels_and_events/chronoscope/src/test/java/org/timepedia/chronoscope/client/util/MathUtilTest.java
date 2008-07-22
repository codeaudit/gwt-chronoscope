/**
 * 
 */
package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author Chad Takahashi &lt;chad@timepedia.org&gt;
 */
public class MathUtilTest extends TestCase {

  public MathUtilTest(String name) {
    super(name);
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
}
