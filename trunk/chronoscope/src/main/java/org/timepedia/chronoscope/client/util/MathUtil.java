package org.timepedia.chronoscope.client.util;

/**
 * Conglomeration of common math functions.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public final class MathUtil {

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
  
  /**
   * Performs 'x modulo y' (not to be confused with the remainder operator '%', which
   * behaves differently when x is negative).
   */
  public static int mod(int x, int y) {
    if (x >= 0) {
      return x % y;
    }
    else {
      int tmp = -x % y; // make x positive before calc'ing remainder
      return (tmp == 0) ? tmp : (y - tmp);
    }
  }
}
