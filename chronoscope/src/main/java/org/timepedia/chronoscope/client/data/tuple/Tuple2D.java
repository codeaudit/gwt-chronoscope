package org.timepedia.chronoscope.client.data.tuple;

/**
 * Equivalent to facade around XYDataset, with getFirst() returning
 * XYDataset.getX(i) and getSecond() returning XYDataset.getY(i)
 */
public interface Tuple2D extends Tuple {
  double getFirst();
  double getSecond();
}
