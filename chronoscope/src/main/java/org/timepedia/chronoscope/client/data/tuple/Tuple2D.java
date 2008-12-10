package org.timepedia.chronoscope.client.data.tuple;

import org.timepedia.exporter.client.Exportable;

/**
 * A homogeneous vector of doubles of fixed dimension D returned by the
 * getDimension() method. The first dimension, by convention, represents
 * strictly increasing values in the domain axis.
 */
public interface Tuple2D extends Exportable {

  /**
   * Returns the value that the specified index within this tuple
   * 
   * @param index - The 0-based index
   */
  double get(int index);
  
  /**
   * The X value, which corresponds to index 0 for
   * {@link Tuple#getCoordinate(int)}.
   */
  double getFirst();

  /**
   * The Y value, which corresponds to index 1 for
   * {@link Tuple#getCoordinate(int)}.
   */
  double getSecond();
  
  /**
   * The number of values in this tuple (also referred to as the tuple's 
   * <i>dimension</i>).
   */
  int size();
  
}
