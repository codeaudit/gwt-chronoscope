package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * An interface used to track modifications to an XYDataset.
 */
public interface XYDatasetListener {

  /**
   * When an XYDataset is modified, this method is invoked with an interval
   * which bounds the span of domain encompassing all the changes that took
   * place.
   */
  void onDatasetChanged(XYDataset dataset, double domainStart, double domainEnd);
}
