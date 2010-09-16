package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.BinaryMipMapStrategy;
import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.ChronoscopeDatasetFactory;

/**
 * Factory for obtaining components intended for use with Chronoscope.
 * 
 * @author chad takahashi
 */
public class ChronoscopeComponentFactory extends ComponentFactory {
  private DatasetFactory datasetFactory;
  
  public ChronoscopeComponentFactory() {
    datasetFactory = new ChronoscopeDatasetFactory();
    
    name2mipmapStrategy.clear();
    name2mipmapStrategy.put("binary", BinaryMipMapStrategy.MEAN);
  }

  @Override
  public DatasetFactory getDatasetFactory() {
    return datasetFactory;
  }

}
