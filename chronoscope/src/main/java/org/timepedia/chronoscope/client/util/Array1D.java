package org.timepedia.chronoscope.client.util;

/**
 * A 1-dimensional array of primitive <tt>double</tt> values, whose
 * primary use is as a row in an {@link Array2D} array.
 * 
 * @see {@link Array2D#getRow(int)}.
 * 
 * @author chad takahashi
 */
public interface Array1D {
  
  /**
   * Returns the value at the index-th position in this array.
   */
  double get(int index);
  
  /**
   * Returns the last value in this array.
   */
  double getLast();
  
  /**
   * Returns the number of elements in this array.
   */
  int size();
  
  /**
   * Applies the specified function to the elements in this array.
   */
  void execFunction(ArrayFunction f);
}
