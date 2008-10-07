package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * Tracks modifications to an {@link XYDatasets} container and its
 * constituent {@link XYDataset} elements.
 */
public interface XYDatasetListener {

  /**
   * When an XYDataset is modified, this method is invoked with an interval
   * which bounds the span of domain encompassing all the changes that took
   * place.
   */
  void onDatasetChanged(XYDataset dataset, double domainStart, double domainEnd);
  
  /**
   * Fired when a dataset is removed from this container.
   * 
   * @param dataset - The dataset that was just removed.  
   */
  void onDatasetRemoved(XYDataset dataset);
  
  /**
   * The dataset that was just added to this container.
   */
  void onDatasetAdded(XYDataset dataset);
  
}
