package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * @author Chad Takahashi
 */
public class DefaultDatasetFactory implements DatasetFactory {

  public Dataset create(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    ArrayDataset2D ds = new ArrayDataset2D(request);
    return ds;
  }

  public MutableDataset createMutable(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    MutableDataset ds = new MutableDataset2D(request);
    return ds;
  }
  
}
