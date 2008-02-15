package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * This interface must be implemented by any dataset implementation that expects
 * modifications to occur. It imposes a contract on modifications, in that all
 * modifications must occur between calls to beginUpdate() and endUpdate()
 */
public interface UpdateableXYDataset extends XYDataset {

  /**
   * Add a XYDatasetListener to this dataset. Listeners are called after execute
   * of endUpdate() if there were any modificatons
   */
  void addXYDatasetListener(XYDatasetListener dataListener);

  /**
   * Prepare this dataset for modification. Must be called before setXY,
   * removeXY or insertXY methods.
   */
  void beginUpdate();

  /**
   * Updates and optimizes the underlying datastructure after a series of
   * modifications. Must be called after beginUpdate()
   */
  void endUpdate();

  /**
   * Remove a XYDatasetListener from this dataset.
   */
  void removeXYDatasetListener(XYDatasetListener dataListener);
}
