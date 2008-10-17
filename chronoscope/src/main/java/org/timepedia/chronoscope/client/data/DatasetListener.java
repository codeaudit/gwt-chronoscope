package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.data.tuple.Tuple;

/**
 * Tracks modifications to an {@link Datasets} container and its
 * constituent {@link Dataset} elements.
 */
public interface DatasetListener<S extends Tuple, T extends Dataset<S>> {

  /**
   * When an XYDataset is modified, this method is invoked with an interval
   * which bounds the span of domain encompassing all the changes that took
   * place.
   */
  void onDatasetChanged(T dataset, double domainStart, double domainEnd);
  
  /**
   * Fired when a dataset is removed from this container.
   * 
   * @param dataset - The dataset that was just removed.
   * @param datasetIndex - the ordinal position of the dataset that
   *    was just removed.  
   */
  void onDatasetRemoved(T dataset, int datasetIndex);
  
  /**
   * The dataset that was just added to this container.
   */
  void onDatasetAdded(T dataset);
  
}
