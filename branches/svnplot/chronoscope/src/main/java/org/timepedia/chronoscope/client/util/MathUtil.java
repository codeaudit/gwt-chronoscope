package org.timepedia.chronoscope.client.util;

/**
 * Conglomeration of common math functions.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public final class MathUtil {


  /**
   * Keeps <tt>value</tt> bounded within the inclusive interval [min,max].
   * More specifically, if <tt>value</tt> falls in the range [min,max], then
   * <tt>value</tt> is returned.  Otherwise, the endpoint closest to  <tt>value</tt>
   * is returned.
   */
  public static double bound(double value, double min, double max) {
    if (value > max) {
      return max;
    }
    if (value < min) {
      return min;
    }
    return value;
  }

  /**
   * Keeps <tt>value</tt> bounded within the inclusive interval [min,max].
   * More specifically, if <tt>value</tt> falls in the range [min,max], then
   * <tt>value</tt> is returned.  Otherwise, the endpoint closest to  <tt>value</tt>
   * is returned.
   */
  public static int bound(int value, int min, int max) {
    if (value > max) {
      return max;
    }
    if (value < min) {
      return min;
    }
    return value;
  }

  /**
   * Returns true only if the specified value is in the range [p1, p2]. Note
   * that this method does not check if [p1, p2] is a valid range (e.g. if p1 is
   * greater than p2). Return value is undefined if any of the inputs are
   * <tt>Double.NaN</tt>, <tt>Double.POSITIVE_INFINITY</tt>, or
   * <tt>Double.NEGATIVE_INFINITY</tt>.
   */
  public static boolean isBounded(double value, double p1, double p2) {
    return value >= p1 && value <= p2;
  }

  /**
   * Returns true only if the specified value is in the range [p1, p2]. Note
   * that this method does not check if [p1, p2] is a valid range (e.g. if p1 is
   * greater than p2).
   */
  public static boolean isBounded(int value, int p1, int p2) {
    return value >= p1 && value <= p2;
  }

  /**
   * Calculates log base 2 of the specified value.
   */
  public static double log2(double value) {
    return Math.log(value) / Math.log(2);
  }
  
  public static int mod(int x, int y) {
    if (x >= 0) {
      return x % y;
    }
    else {
      int tmp = -x % y; // make x positive before calc'ing remainder
      return (tmp == 0) ? tmp : (y - tmp);
    }
  }
  
  /**
   * Performs 'x modulo y' (not to be confused with the remainder operator '%', which
   * behaves differently when x is negative).
   */
  public static double mod(double x, double y) {
    if (x >= 0) {
      return x % y;
    }
    else {
      double tmp = -x % y; // make x positive before calc'ing remainder
      return (tmp == 0) ? tmp : (y - tmp);
    }
  }
}
