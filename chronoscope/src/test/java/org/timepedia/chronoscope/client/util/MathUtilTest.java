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
