package org.timepedia.chronoscope.client;

import org.timepedia.util.junit.ObjectSmokeTest;
import org.timepedia.util.junit.TestObjectFactory;

import junit.framework.TestCase;

/**
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public class FocusTest extends TestCase {
  private TestObjectFactory focusFactory;

  public FocusTest(String name) {
    super(name);

    this.focusFactory = new TestObjectFactory() {
      final int instanceCount = 101;

      public Object getInstance(int index) {
        boolean returnZeroArgConstructor = (index == instanceCount - 1);
        if (returnZeroArgConstructor) {
          return new Focus();
        } else {
          int dataSetIndex = index / 10;
          int pointIndex = index % 10;
          Focus focus = new Focus();
          focus.setPointIndex(pointIndex);
          focus.setDatasetIndex(dataSetIndex);
          return focus;
        }
      }

      public int instanceCount() {
        return instanceCount;
      }
    };
  }

  public void testObjectEssentials() {
    ObjectSmokeTest smokeTest = new ObjectSmokeTest(this.focusFactory);
    smokeTest.testAll();
  }

}
