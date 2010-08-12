/**
 * 
 */
package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

/**
 * @author chad takahashi
 */
public class ExtremaArrayFunctionTest extends TestCase {

  public void testFn() {
    ExtremaArrayFunction fn = new ExtremaArrayFunction();
    fn.exec(new double[] {30.0, 40.0, 20.0, 10.0}, 3); // should ignore last value
    assertEquals(new Interval(20.0, 40.0), fn.getExtrema());
    
    fn.exec(new double[] {10.0}, 1);
    assertEquals(new Interval(10.0, 10.0), fn.getExtrema());
    
    fn.exec(new double[] {}, 0);
    assertNull(fn.getExtrema());
  }
}
