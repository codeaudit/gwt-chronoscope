package org.timepedia.util.junit;

import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Object-oriented wrapper around a primitive double array to make array
 * manipulation less cumbersome.
 * <p>
 * This class was created for use with unit tests.  That said, it makes no attempt
 * to be efficient with respect to speed or memory.
 * 
 * @author chad takahashi
 */
public final class OODoubleArray {
  private double[] a;

  public OODoubleArray(double[] a) {
    ArgChecker.isNotNull(a, "a");
    this.a = a;
  }

  /**
   * Makes a copy of this object.
   */
  public OODoubleArray copy() {
    double[] aCopy = new double[a.length];
    System.arraycopy(a, 0, aCopy, 0, a.length);
    return new OODoubleArray(aCopy);
  }

  /**
   * Returns the backing array.
   */
  public double[] getArray() {
    return a;
  }

  /**
   * Returns the last element of this array
   */
  public double getLast() {
    return a[a.length - 1];
  }
  
  public int size() {
    return a.length;
  }
  
  public OODoubleArray getSubArray(int startIdx, int endIdx) {
    int length = (endIdx - startIdx) + 1;
    double[] subArray = new double[length];
    System.arraycopy(this.a, startIdx, subArray, 0, length); 
    return new OODoubleArray(subArray);
  }
  
  /**
   * Removes the last element from the backing array that this object wraps.
   */
  public OODoubleArray removeLast() {
    double[] a2 = new double[this.a.length - 1];
    System.arraycopy(this.a, 0, a2, 0, a2.length);
    return new OODoubleArray(a2);
  }
  
  public String toString() {
    return java.util.Arrays.toString(a);
  }
}
