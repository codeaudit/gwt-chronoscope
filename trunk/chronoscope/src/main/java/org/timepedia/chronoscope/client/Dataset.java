package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

/**
 * Models a multiresolution dataset containing 0 or more {@link Tuple2D} points.
 * <p>
 * A dataset allows arbitrary Tuples to be returned from each sample index. The
 * tuples returned are not guaranteed to be mutation free; that is, they should
 * be considered flyweight objects to be used and discarded before the next
 * invocation of {@link #getFlyweightTuple(int)}. Call {@link Tuple2D#copy()}
 * to obtain a persistent tuple.
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
 * 
 * @gwt.exportPackage chronoscope
 * @gwt.export
 */
@ExportPackage("chronoscope")
@Export
public interface Dataset<T extends Tuple2D> extends Exportable {

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
   * Return the minimum domain value in this dataset, which corresponds to the
   * first value in the tuple at index 0.
   */
  double getDomainBegin();

  /**
   * Return the maxiumum domain value in this dataset, which corresponds to the
   * first value in the tuple at index <tt>{@link #getNumSamples() - 1}<?tt>.
   */
  double getDomainEnd();

  /**
   * Returns the tuple associated with the specified domain index at mip level
   * 0.
   */
  @NoExport
  T getFlyweightTuple(int index);

  /**
   * Returns the tuple associated with the specified domain index and mip level.
   */
  @NoExport
  T getFlyweightTuple(int index, int mipLevel);

  /**
   * Return a unique identifier for this dataset.
   */
  String getIdentifier();

  /**
   * Returns the maximum value at the specified tuple coordinate across all
   * tuples in this dataset.
   */
  double getMaxValue(int coordinate);

  /**
   * Returns the minimum value at the specified tuple coordinate across all
   * tuples in this dataset.
   */
  double getMinValue(int coordinate);

  /**
   * Returns a key representing the preferred {@link DatsetRenderer} to use when 
   * drawing this dataset.
   */
  @Deprecated
  String getPreferredRenderer();
  
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
   * Returns the fixed number of components that each tuple within this dataset
   * can hold (e.g. a 2-tuple, 5-tuple, etc.).
   */
  int getTupleLength();
  
  /**
   * Return the domain value for the given index on level 0
   */
  double getX(int index);

  /**
   * Return the domain value for the given index on the given mip level
   */
  double getX(int index, int level);

}
