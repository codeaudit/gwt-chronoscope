package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.util.Interval;

/**
 * @author chad takahashi
 */
public class MutableDataset2DTest extends TestCase {
  private DatasetFactory dsFactory = new ChronoscopeDatasetFactory();
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  
  public void testAppend() {
    DatasetRequest request = 
      dsMaker.newRequest(new double[] {100, 200, 250}, new double[] {1, 2, 3});
    MutableDataset ds = dsFactory.createMutable(request);
    

    assertEquals(50.0, ds.getMinDomainInterval());
    assertEquals(new Interval(1, 3), ds.getExtrema(1));
    
    // Add a new point whose distance from the previous point is 25 ms.  This should
    // decrease the minimum interval reported by the Dataset.
    ds.mutate(Mutation.append(275, 4));
    assertEquals(25.0, ds.getMinDomainInterval());
    assertEquals(new Interval(1, 4), ds.getExtrema(1));
  }
}
