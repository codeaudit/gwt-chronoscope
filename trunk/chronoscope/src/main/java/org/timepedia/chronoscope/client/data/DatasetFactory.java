package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;


/**
 * Factory for creating different types of {@link Dataset} objects.
 * 
 * @author Chad Takahashi
 */
public interface DatasetFactory<T extends Tuple2D> {

  Dataset<T> create(DatasetRequest request);

  MutableDataset<T> createMutable(DatasetRequest request);
  
}
