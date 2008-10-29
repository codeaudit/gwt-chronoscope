package org.timepedia.chronoscope.client.data.mock;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.DefaultDatasetFactory;
import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.DatasetRequest;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * Factory for creating in-memory mock datasets for testing.
 * 
 * @author chad takahashi
 */
public class MockDatasetFactory {
  private DatasetFactory dsFactory = new DefaultDatasetFactory();
  
  /**
   * Creates a basic sine wave composed of 1000 points spaced 1 day
   * apart starting at year 1970.
   */
  public Dataset getBasicDataset() {
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
    
    DatasetRequest.Basic request = new DatasetRequest.Basic();
    request.setIdentifier("mock");
    request.setAxisId("none");
    request.setLabel("Mock");
    request.addTupleSlice(domainValues);
    request.addTupleSlice(rangeValues);
    
    return dsFactory.create(request);
  }
}
