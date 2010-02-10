package org.timepedia.chronoscope.client.util;

/**
 * Calculates the minimum and maximum values across all elements
 * in an {@link Array1D} object.
 * 
 * @author chad takahashi
 */
public class ExtremaArrayFunction implements ArrayFunction {
  private Interval extrema;
  
  public void exec(double[] data, int arrayLength) {
    if (arrayLength == 0) {
      extrema = null;
    }
    else {
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      
      for (int i = 0; i < arrayLength; i++) {
        double value = data[i];
        if (Double.isNaN(value)) {
          continue;
        }
        min = Math.min(min, value);
        max = Math.max(max, value);
      }
      
      extrema = new Interval(min, max);
    }
  }
  
  public Interval getExtrema() {
    return this.extrema;
  }
}
