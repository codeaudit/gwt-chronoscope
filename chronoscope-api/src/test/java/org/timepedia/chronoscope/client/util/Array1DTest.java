package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;


/**
 * @author Chad Takahashi
 */
public class Array1DTest extends TestCase {
  
  public void testToArray() {
    Array1D a = newArray(new double[] {10, 20, 30});
    double[] aCopy = a.toArray();
    assertEquals(a.size(), aCopy.length);
    for (int i = 0; i < a.size(); i++) {
      assertEquals(a.get(i), aCopy[i]);
    }
  }
  
  public void testExecFunction() {
    Array1D a = newArray(new double[] {10, 20, 30, 40});
    SumArrayFunction sum = new SumArrayFunction();
    a.execFunction(sum);
    assertEquals((double)(10+20+30+40), sum.result);
  }
  
  public void testGetLast() {
    assertEquals(3.0, newArray(new double[] {4.0, 2.0, 3.0}).getLast());
    assertEquals(11.0, newArray(new double[] {9.0, 11.0}).getLast());
    assertEquals(9.0, newArray(new double[] {9.0}).getLast());
    
    try {
      assertEquals(9.0, newArray(new double[] {}).getLast());
      fail("Expected exception");
    }
    catch (IllegalStateException e) {}
  }
  
  public void testIsEmpty() {
    Array1D a;
    
    a = newArray(new double[] {});
    assertTrue(a.isEmpty());
    
    a = newArray(new double[] {1});
    assertFalse(a.isEmpty());
  }

  public void testSize() {
    Array1D a;
    
    a = newArray(new double[] {});
    assertEquals(0, a.size());
    
    a = newArray(new double[] {1});
    assertEquals(1, a.size());

    a = newArray(new double[] {1, 2, 3, 4, 5});
    assertEquals(5, a.size());
  }

  public static Array1D newArray(double[] a) {
    double[][] dummy2dArray = new double[2][];
    dummy2dArray[0] = a;
    dummy2dArray[1] = new double[] {1, 2, 3}; // dummy values
    
    JavaArray2D a2d = new JavaArray2D(dummy2dArray);
    return a2d.getRow(0);
  }
  
  private static final class SumArrayFunction implements ArrayFunction {
    public double result;

    public void exec(double[] data, int arrayLength) {
      result = 0;
      for (int i = 0; i < arrayLength; i++) {
        if (Double.isNaN(data[i])) {
          continue;
        }
        result += data[i];
      }
    }
  }

}
