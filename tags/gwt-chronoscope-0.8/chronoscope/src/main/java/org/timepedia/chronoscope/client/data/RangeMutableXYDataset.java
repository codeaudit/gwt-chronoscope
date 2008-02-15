package org.timepedia.chronoscope.client.data;

/**
 * Interface used by dataset implementations that support inplace modification
 * of Y values
 */
public interface RangeMutableXYDataset extends UpdateableXYDataset {

  /**
   * Mutate an existing point Y value in the dataset at index i. Must be called
   * after beginUpdate()
   */
  void setXY(int index, double y);
}
