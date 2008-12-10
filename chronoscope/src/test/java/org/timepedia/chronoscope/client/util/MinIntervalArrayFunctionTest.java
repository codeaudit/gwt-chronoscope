package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author chad takahashi
 */
public class MinIntervalArrayFunctionTest extends TestCase {

  public void testExec() {
    MinIntervalArrayFunction fn = new MinIntervalArrayFunction();
    fn.exec(new double[] {10, 30, 32,    31}, 3);
    assertEquals(2.0, fn.getMinInterval());

    fn.exec(new double[] {}, 0);
    assertEquals(0.0, fn.getMinInterval());

    fn.exec(new double[] {5}, 1);
    assertEquals(0.0, fn.getMinInterval());
    
    fn.exec(new double[] {-2, -3, 4, 2}, 4);
    assertEquals(1.0, fn.getMinInterval());
  }
}
