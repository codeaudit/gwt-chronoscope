package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;


/**
 * Factory for creating different types of {@link Dataset} objects.
 * 
 * @author Chad Takahashi
 */
public interface DatasetFactory {

  Dataset create(DatasetRequest request);

  MutableDataset createMutable(DatasetRequest request);
  
}
