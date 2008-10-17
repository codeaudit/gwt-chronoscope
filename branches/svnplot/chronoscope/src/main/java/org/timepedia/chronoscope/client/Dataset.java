package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.tuple.Tuple;
import org.timepedia.exporter.client.Exportable;

/**
 * Models a multiresolution dataset containing 0 or more {@link Tuple} points.
 * <p>
 * A dataset allows arbitrary Tuples to be returned from each sample
 * index. The tuples returned are not guaranteed to be mutation free; that is,
 * they should be considered flyweight objects to be used and discarded before
 * the next invocation of {@link #getFlyweightTuple(int)}. Call
 * {@link Tuple#copy()} to obtain a persistent tuple.
 * <p>
 * A multiresolution dataset is a dataset consisting of multiple levels, with
 * level 0 being the bottom most level containing the original datasets. Levels
 * 1 and above represent a 'zoomed out' view of the data, filtered,
 * interpolated, scaled, or decimated as appropriate. The only requirement is
 * that if M and N are two levels in the dataset, and M > N, then
 * getNumSamples(M) < getNumSamples(N). In other words, the number of samples
 * (the resolution) must strictly decrease as the level increases. A useful
 * visualization is to imagine levels stacking on top of one another like a
 * pyramid. There is no requirement that the pyramid contain a level Z such that
 * getNumSamples(Z) == 1, but in practice, getNumSamples(Z) should be less than
 * XYPlot.getMaxDrawablePoints().
 * <p>
 * It is recommended to use a method that makes successive levels half the size
 * of former levels, so that the height of the pyramid is
 * <tt>log_2(num_samples)</tt>.
 */
public interface Dataset<T extends Tuple> extends Exportable {

  /**
   * Returns <em>approximately</em> the smallest domain interval between two
   * points.
   */
  double getApproximateMinimumInterval();

  /**
   * Return an id used to identify the axis this dataset should be assigned to,
   * typically the physical units (e.g. meters/second). Datasets with identical
   * axis ids will, by default, be allocated on the same axis.
   */
  String getAxisId();

  /**
   * Return a unique identifier for this dataset.
   */
  String getIdentifier();

  /**
   * Returns the number of samples in the dataset (mip level 0).
   */
  int getNumSamples();

  /**
   * Return the number of samples in the dataset at the specified mip level.
   */
  int getNumSamples(int level);

  /**
   * Return a label that may be used in a chart legend for this dataset.
   */
  String getRangeLabel();

  /**
   * Returns the tuple associated with the specified domain index at mip level
   * 0.
   */
  T getFlyweightTuple(int index);

  /**
   * Returns the tuple associated with the specified domain index and mip level.
   */
  T getFlyweightTuple(int index, int mipLevel);

}
