package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.data.AbstractDataset;
import org.timepedia.chronoscope.client.data.MipMapChain;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

import java.util.Arrays;

/**
 * @author Chad Takahashi
 *
 */
public class UtilTest extends TestCase {

  public void testBinarySearch() {
    double[] domain = new double[] {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    Array1D a = Array1DTest.newArray(domain);
    
    // Case 1: Verify that exact key matches are found
    for (int i = 0; i < domain.length; i++) {
      assertEquals(i, Util.binarySearch(a, domain[i]));
    }
    
    // Case 2: Verify that a non-matching key returns the index to the closest
    // domain value to the right.
    for (int i = 0; i < domain.length - 1; i++) {
      // Pick a search key that's between index [i] and [i+1]
      double searchKey = (domain[i] + domain[i + 1]) / 2.0;
      int expectedIndex = i + 1; // closest matching index to the right
      assertEquals(expectedIndex, Util.binarySearch(a, searchKey));
    }
    
    // Case 3: search key that's smaller than domain[0] should return index 0
    assertEquals(0, Util.binarySearch(a, domain[0] - 1.0));

    // Case 4: search key that's greater than domain[lastIndex] should
    // return lastIndex.
    final int lastIndex = domain.length - 1;
    assertEquals(lastIndex, Util.binarySearch(a, domain[lastIndex] + 1.0));
  }
  
  public void testSearch() {
    double[] a = new double[] {2, 4, 6, 8};
    assertEquals(-1, Util.search(a, 1, 0));
    assertEquals(-1, Util.search(a, 2, 0));
    
    assertEquals(0, Util.search(a, 4, 0));
    assertEquals(1, Util.search(a, 6, 0));
    assertEquals(2, Util.search(a, 8, 0));
    assertEquals(3, Util.search(a, 9, 0));
    assertEquals(3, Util.search(a, 10, 0));
    
    assertEquals(-1, Util.search(new double[] {}, 99, 0));
  }
  
  public void testCopyArray() {
    double[] a = {1, 3, 2};
    assertTrue(Arrays.equals(a, Util.copyArray(a)));
    assertTrue(Arrays.equals(new double[] {1, 3}, Util.copyArray(a, 2)));
    
    double[][] srcArray2D = new double[][] {
        {1, 2, 3},
        null,
        {4, 5}
        };
    double[][] a2dCopy = Util.copyArray(srcArray2D);
    for (int i = 0; i < srcArray2D.length; i++) {
      double[] srcRow = srcArray2D[i];
      if (srcRow != null) {
        assertTrue(Arrays.equals(srcRow, a2dCopy[i]));
      }
      else {
        assertNull(a2dCopy[i]);
      }
    }
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
  
  
  private static final class JUnitDataset extends AbstractDataset<Tuple2D> {
    private double[] domain;
    
    public JUnitDataset(double[] domain) {
      this.domain = domain;
    }
    
    public String getAxisId(int rangeTupleCoordinate) {
      throw new UnsupportedOperationException();
    }
    
    public int getNumSamples() {
      return this.domain.length;
    }

    public int getTupleLength() {
      return 2;
    }

    public double getX(int index) {
      return domain[index];
    }

    public Tuple2D getFlyweightTuple(int index) {
      throw new UnsupportedOperationException();
    }

    public Interval getRangeExtrema(int tupleCoordinate) {
      throw new UnsupportedOperationException();
    }

    public MipMapChain getMipMapChain() {
      throw new UnsupportedOperationException();
    }

    public String toJson() {
      return null;
    }

    public void clear() {
      domain = null;
    }
  }
}
