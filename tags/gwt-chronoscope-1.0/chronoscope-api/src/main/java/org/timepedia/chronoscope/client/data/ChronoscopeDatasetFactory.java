package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Supports creation of 2-tuple datasets.
 * 
 * @author Chad Takahashi
 */
public class ChronoscopeDatasetFactory implements DatasetFactory {

  public final Dataset create(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    if (!supportsTupleLength(request.getTupleLength())) {
      throw new RuntimeException(request.getTupleLength() + "-tuple dataset " + 
          "creation not supported");
    }
    
    return newDataset(request);
  }

  public MutableDataset createMutable(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    MutableDataset ds = new MutableDatasetND(request);
    return ds;
  }
  
  protected boolean supportsTupleLength(int tupleLength) {
    return tupleLength == 2;
  }
  
  protected Dataset newDataset(DatasetRequest request) {
    return new ArrayDataset2D(request);
  }
}
