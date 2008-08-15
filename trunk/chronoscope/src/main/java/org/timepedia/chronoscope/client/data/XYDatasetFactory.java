package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;


/**
 * Factory for creating different types of {@link XYDataset} objects.
 * 
 * @author Chad Takahashi
 */
public interface XYDatasetFactory {

  XYDataset create(XYDatasetRequest request);

  MutableXYDataset createMutable(XYDatasetRequest request);
  
}
