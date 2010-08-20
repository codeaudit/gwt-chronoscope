package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.MipMapChain;
import org.timepedia.chronoscope.client.data.MipMapRegion;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.Interval;
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
   * Returns the smallest domain interval between any two consecutive points
   * within the dataset.
   */
  double getMinDomainInterval();

  /**
   * Finds the best MipMap (highest resolution) containing the the given region,
   * where the number of points in the region do not exceed <tt>maxSamples<tt>
   * @param region the domain interval used for the search
   * @param maxSamples the maximum number of points in the MipMap's region
   * @return a MipMap and pair of start and end indices in the MipMap
   */
  @NoExport
  MipMapRegion getBestMipMapForInterval(Interval region, int maxSamples);

  /**
   * Provides access to the ordered set of {@link MipMap} objects, which
   * represent this dataset at decreasing levels of resolution.
   */
  @NoExport
  MipMapChain getMipMapChain();

  /**
   * Return an id used to identify the axis this dataset should be assigned to,
   * typically the physical units (e.g. meters/second). Datasets with identical
   * axis ids will, by default, be allocated on the same axis.
   */
  String getAxisId(int rangeTupleCoordinate);

  /**
   * Returns an interval representing the min and max values for the specified
   * range tuple coordinate.
   */
  Interval getRangeExtrema(int rangeTupleCoordinate);

  /**
   * Returns an interval that contains the minimum and maximum domain values
   * of this dataset's domain.
   */
  Interval getDomainExtrema();
  
  /**
   * Returns the tuple associated with the specified data point index.
   */
  @NoExport
  T getFlyweightTuple(int index);

  /**
   * Return a unique identifier for this dataset.
   */
  String getIdentifier();

  /**
   * The min/max range values that {@link RangeAxis} will use as its bounds for computing
   * the range tick values.  If null, then the actual min/max range values of this dataset
   * will be used instead.
   */
  @NoExport
  Interval getPreferredRangeAxisInterval();

  /**
   * Returns a key representing the preferred {@link DatsetRenderer} to use when
   * drawing this dataset.
   */
  @Deprecated
  String getPreferredRenderer();

  /**
   * Returns the number of samples in this dataset.
   */
  int getNumSamples();

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
   * Return the domain value for the given data point index on mip level 0.
   */
  double getX(int index);

}
