package org.timepedia.chronoscope.client.data;

/**
 * Helper class for constructing datasets for use with JUnit tests.
 * 
 * @author chad takahashi
 *
 */
public final class DatasetRequestMaker {
  
  public DatasetRequest.Basic newRequest(double[] domain, double[] range) {
    DatasetRequest.Basic request = new DatasetRequest.Basic();
    request.setAxisId("Dummy_Axis_Id");
    request.setLabel("Dummy_Range_Label");
    request.setDefaultMipMapStrategy(DefaultMipMapStrategy.MEAN);
    request.setDomain(domain);
    request.setRange(range);
    
    return request;
  }
}
