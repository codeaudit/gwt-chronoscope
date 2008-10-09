package org.timepedia.chronoscope.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * An interface modeling a multiresolution dataset of two coordinates.
 * <p>
 * A multiresolution dataset is a dataset consisting of multiple levels, with
 * level 0 being the bottom most level containing the original datasets. Levels
 * 1 and above represent a 'zoomed out' view of the data, filtered,
 * interpolated, scaled, or decimated as appropriate. The only requirement is
 * that if M and N are two levels in the dataset, and M > N, then
 * getNumSamples(M) < getNumSamples(N). In other words, the number of samples
 * (the resolution) must strictly decrease as the level increases.  A useful 
 * visualization is to imagine levels stacking on top of one another like a 
 * pyramid. There is no requirement that the pyramid contain a level Z such that 
 * getNumSamples(Z) == 1, but in practice, getNumSamples(Z) should be less than 
 * XYPlot.getMaxDrawablePoints().
 * <p>
 * It is recommended to use a method that makes successive levels half the size
 * of former levels, so that the height of the pyramid is <tt>log_2(num_samples)</tt>.
 *
 * @gwt.exportPackage chronoscope
 * @gwt.export
 */
@ExportPackage("chronoscope")
@Export
public interface XYDataset extends Exportable {

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
   * Return the minimum X value datapoint in this dataset. Usually the same as
   * getX(0) except for Regional datasets.
   */
  double getDomainBegin();

  /**
   * Return the maximum X value datapoint in this dataset. Usually the same as
   * getX(getNumSamples()-1) except for Regional datasets.
   */
  double getDomainEnd();

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
   * Return the minimum Y value of mip level 0.
   */
  double getRangeBottom();

  /**
   * Return a label that may be used in a chart legend for this dataset.
   */
  String getRangeLabel();

  /**
   * Return the maximum Y value of mip level 0.
   */
  double getRangeTop();

  /**
   * Return the X value for the given index on level 0
   */
  double getX(int index);

  /**
   * Return the X value for the given index on the given mip level
   */
  double getX(int index, int level);

  /**
   * Return the Y value for the given index on mip level 0.
   */
  double getY(int index);

  /**
   * Return the Y value for the given index on the given mip level.
   */
  double getY(int index, int level);
}
