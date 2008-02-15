package org.timepedia.chronoscope.client.data;

/**
 * This interface provides a mechanism for appending elements to an XY dataset
 * elements, such as dealing with incoming sensor network data or financial
 * data.
 */
public interface AppendableXYDataset extends UpdateableXYDataset {

  /**
   * Insert a new point into this dataset. Must be called after beginUpdate()
   * Append
   */
  void insertXY(double x, double y);
}
