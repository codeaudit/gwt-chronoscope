package org.timepedia.chronoscope.client.data.tuple;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * A Tuple dataset is an extension of the XYDataset interface that allows
 * arbitrary Tuples to be returned from each sample index. The tuples returned
 * are not guaranteed to be mutation free, that is, they should be considered
 * flyweight objects to be used and discarded before calling getFlyweightTuple().
 * More persistent references to a tuple can be held by calling Tuple.copy()
 */
public interface TupleDataset<T extends Tuple> extends XYDataset {
  T getFlyweightTuple(int index);
  T getFlyweightTuple(int index, int mipLevel);
}
