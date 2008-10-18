package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;


/**
 * Factory for creating different types of {@link Dataset} objects.
 * 
 * @author Chad Takahashi
 */
public interface DatasetFactory {

  Dataset create(DatasetRequest request);

  MutableDataset2D createMutable(DatasetRequest request);
  
}
