package org.timepedia.chronoscope.client.data.tuple;

/**
 * A tuple that represents an ordered pair of values, typically a point
 * in 2-dimensional space.
 */
public interface Tuple2D extends Tuple {
  
  /**
   * The X value, which corresponds to index 0 for {@link Tuple#getCoordinate(int)}.
   */
  double getFirst();

  /**
   * The Y value, which corresponds to index 1 for {@link Tuple#getCoordinate(int)}.
   */
  double getSecond();
  
}
