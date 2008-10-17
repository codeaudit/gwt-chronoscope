package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;

/**
 * An interface modeling a multiresolution dataset having exactly two
 * coordinates -- X (representing timestamp values in the domain)
 * and Y (representing the associated range values).
 * 
 * @gwt.exportPackage chronoscope
 * @gwt.export
 */
@ExportPackage("chronoscope")
@Export
public interface XYDataset<T extends Tuple2D> extends Dataset<T> {

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
   * Return the minimum Y value of mip level 0.
   */
  double getRangeBottom();

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
