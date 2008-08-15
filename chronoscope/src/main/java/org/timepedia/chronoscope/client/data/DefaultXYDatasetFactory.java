package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * @author Chad Takahashi
 */
public class DefaultXYDatasetFactory implements XYDatasetFactory {

  public XYDataset create(XYDatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    ArrayXYDataset ds = new ArrayXYDataset(request);
    return ds;
  }

  public MutableXYDataset createMutable(XYDatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    MutableXYDataset ds = new MutableXYDataset(request);
    return ds;
  }
  
}
