package org.timepedia.chronoscope.client.data.tuple;

import org.timepedia.exporter.client.Exportable;

/**
 * An n-dimensional vector of primitive <tt>double</tt> values, where 
 * <i>n</i> is denoted by {@link #size()}.
 */
public interface Tuple2D extends Exportable {

  /**
   * Returns the value that the specified index within this tuple
   * 
   * @param index - The 0-based index
   */
  double getRange(int index);
  
  /**
   * The domain value of the data point that this tuple represents.
   */
  double getDomain();

  /**
   * The range value (or if this is a tuple having 3 or more dimensions,
   * the first of several range values).
   */
  double getRange0();
  
  /**
   * The number of values in this tuple (also referred to as the tuple's 
   * <i>dimension</i>).
   */
  int size();
  
}
