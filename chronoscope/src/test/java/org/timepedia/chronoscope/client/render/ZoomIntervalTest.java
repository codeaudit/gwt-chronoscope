/**
 * 
 */
package org.timepedia.chronoscope.client.render;

import junit.framework.TestCase;

import org.timepedia.util.junit.ObjectSmokeTest;
import org.timepedia.util.junit.TestObjectFactory;

/**
 * @author Chad Takahashi
 *
 */
public class ZoomIntervalTest extends TestCase {
  private TestObjectFactory zoomIntervalFactory;
  
  public ZoomIntervalTest(String name) {
    super(name);

    this.zoomIntervalFactory = new TestObjectFactory() {
      final String[] names = new String[] {"1d","5d","1m","6m","1y","max"};
      final double[] intervals = new double[] {1, 5, 30, 30*6, 365, Double.MAX_VALUE};
      final int instanceCount = names.length;

      public Object getInstance(int index) {
          ZoomInterval z = new ZoomInterval(names[index], intervals[index]);
          return z;
      }

      public int instanceCount() {
        return instanceCount;
      }
    };
  }
  
  public void testCompareTo() {
    assertTrue(new ZoomInterval("x", 100).compareTo(new ZoomInterval("x", 200)) < 0);
    assertTrue(new ZoomInterval("x", 200).compareTo(new ZoomInterval("x", 100)) > 0);
    assertTrue(new ZoomInterval("x", 100).compareTo(new ZoomInterval("x", 100)) == 0);
  }
  
  public void testObjectEssentials() {
    ObjectSmokeTest smokeTest = new ObjectSmokeTest(this.zoomIntervalFactory);
    smokeTest.testAll();
  }

}
