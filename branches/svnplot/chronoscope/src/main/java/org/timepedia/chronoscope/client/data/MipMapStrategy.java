package org.timepedia.chronoscope.client.data;

/**
 * Strategy for converting an ordered set of data points into multiple coarser
 * resolutions (i.e. "mipmapping") for the primary purpose of speeding up visual
 * rendering.
 * 
 * @author Chad Takahashi
 */
public interface MipMapStrategy {

  /**
   * Calculates the multiDomain (domain values at all supported levels of
   * resolution) for the specified domain list.
   */
  Array2D calcMultiDomain(double[] domain);

  /**
   * Calculates the multiRange (range values at all supported levels of
   * resolution) for the specified range list.
   */
  Array2D calcMultiRange(double[] range);

  /**
   * Allows insertion of a new domain value to the end of an existing
   * multiDomain (optional operation).
   */
  void appendDomainValue(double x, Array2D multiDomain);

  /**
   * Allows insertion of a new range value to the end of an existing multiRange
   * (optional operation).
   */
  void appendRangeValue(double y, Array2D multiRange);

  /**
   * Updates the Y-value of an existing datapoint within a dataset (optional
   * operation).
   * 
   * @param pointIndex - the 0-based index of the datapoint.
   * @param y - the range value to be updated.
   */
  void setRangeValue(int pointIndex, double y, Array2D multiRange);

}
