package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

/**
 * @author chad takahashi
 */
public class MutableXYDatasetTest extends TestCase {
  private XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  
  public void testAppend() {
    XYDatasetRequest request = 
      dsMaker.newRequest(new double[] {100, 200, 250}, new double[] {1, 2, 3});
    MutableXYDataset ds = dsFactory.createMutable(request);
    
    assertEquals(50.0, ds.getApproximateMinimumInterval());
    assertEquals(1.0, ds.getRangeBottom());
    assertEquals(3.0, ds.getRangeTop());
    
    // Add a new point whose distance from the previous point is 25 ms.  This should
    // decreas the minimum interva reported by the Dataset.
    ds.mutate(Mutation.append(275, 4));
    assertEquals(25.0, ds.getApproximateMinimumInterval());
    assertEquals(1.0, ds.getRangeBottom());
    assertEquals(4.0, ds.getRangeTop());
  }
}
