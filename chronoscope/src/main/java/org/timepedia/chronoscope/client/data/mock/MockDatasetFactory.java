package org.timepedia.chronoscope.client.data.mock;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.DefaultXYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetRequest;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * Factory for creating in-memory mock datasets for testing.
 * 
 * @author chad takahashi
 */
public class MockDatasetFactory {
  private XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
  
  /**
   * Creates a basic sine wave composed of 1000 points spaced 1 day
   * apart starting at year 1970.
   */
  public XYDataset getBasicDataset() {
    int numSamples = 1000;
    
    ChronoDate d = ChronoDate.getSystemDate();
    d.set(TimeUnit.YEAR, 1970);
    d.set(TimeUnit.MONTH, 0);
    d.set(TimeUnit.DAY, 1);

    double[] domainValues = new double[numSamples];
    double[] rangeValues = new double[numSamples];
    
    for (int i = 0; i < numSamples; i++) {
      double tmp = 5.0 * (double) i / (double)numSamples;
      double ry = Math.sin(Math.PI * tmp) / Math.exp(tmp / 5.0);
      
      domainValues[i] = d.getTime();
      rangeValues[i] = ry;
      d.add(TimeUnit.DAY, 1);
    }
    
    XYDatasetRequest.Basic request = new XYDatasetRequest.Basic();
    request.setIdentifier("mock");
    request.setAxisId("none");
    request.setLabel("Mock");
    request.setDomain(domainValues);
    request.setRange(rangeValues);
    
    return dsFactory.create(request);
  }
}
