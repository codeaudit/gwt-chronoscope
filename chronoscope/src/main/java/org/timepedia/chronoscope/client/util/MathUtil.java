package org.timepedia.chronoscope.client.util;

/**
 * Conglomeration of common math functions.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public final class MathUtil {
  
  /**
   * Calculates log base 2 of the specified value.
   */
  public static double log2(double value) {
    return Math.log(value) / Math.log(2);
  }

}
