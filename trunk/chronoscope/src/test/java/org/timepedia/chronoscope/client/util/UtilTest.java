package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author Chad Takahashi
 *
 */
public class UtilTest extends TestCase {

  public UtilTest(String name) {
    super(name);
  }
  
  public void testArraycopy() {
    double[] a1 = new double[] {1.0, 2.0, 3.0};
    double[] a2 = new double[a1.length];
    Util.arraycopy(a1, 0, a2, 0, a1.length);
    assertTrue(java.util.Arrays.equals(a1, a2));
  }
  
  public void testIsEqual() {
    Integer x = new Integer(42);
    
    assertTrue(Util.isEqual(null, null));
    assertTrue(Util.isEqual(x, x)); // same reference
    assertTrue(Util.isEqual(new Integer(10), new Integer(10))); // same value
  
    assertFalse(Util.isEqual(new Integer(10), new Integer(12)));
    assertFalse(Util.isEqual(null, x));
    assertFalse(Util.isEqual(x, null));
  }
}
