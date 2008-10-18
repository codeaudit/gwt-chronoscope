/**
 * 
 */
package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.Mutation;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * Dataset that permits certain types of mutations (e.g. appending new data
 * points, modifying the state of existing data points).
 * 
 * @author chad takahashi
 */
public interface MutableDataset<T extends Tuple2D> extends Dataset<T> {

  /**
   * Adds the specified listener to the collection of listeners to be notified
   * when changes to this dataset occur.
   */
  void addListener(DatasetListener<T> listener);

  /**
   * Removes the specified listener from the collection of listeners to be
   * notified when changes to this dataset occur.
   */
  public void removeListener(DatasetListener<T> listener);

  /**
   * Applies the specified mutation to this dataset.
   */
  public void mutate(Mutation mutation);

}
