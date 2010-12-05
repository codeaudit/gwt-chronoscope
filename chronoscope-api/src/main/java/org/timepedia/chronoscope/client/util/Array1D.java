package org.timepedia.chronoscope.client.util;

/**
 * A 1-dimensional array of primitive <tt>double</tt> values, whose
 * primary use is as a row in an {@link Array2D} array.
 * 
 * @see Array2D#getRow(int)
 * 
 * @author chad takahashi
 */
public interface Array1D {
  
  /**
   * Returns the primitive <tt>double</tt> array that backs this
   * object.  Note that the length of the backing array might 
   * be greater than what {@link #size()} reports if this is
   * a growable array.
   * <p>
   * This method should only be used when maximum performance 
   * is needed.
   */
  double[] backingArray();
  
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
   * Returns true if this array has 0 elements.
   */
  boolean isEmpty();
  
  /**
   * Applies the specified function to the elements in this array.
   */
  void execFunction(ArrayFunction f);
  
  /**
   * Returns a copy of this array as a primitive double array.
   */
  double[] toArray();
  
  void clear();
}
