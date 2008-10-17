package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.junit.OODoubleArray;

/**
 * @author chad takahashi
 */
public class ArrayXYDatasetTest extends TestCase {
  private XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  
  public void testSinglePoint() {
    XYDatasetRequest request = dsMaker.newRequest(new double[] {1000}, new double[] {10});
    XYDataset ds = dsFactory.create(request);

    assertEquals(1, ds.getNumSamples());
    assertEquals(1000.0, ds.getDomainBegin());
    assertEquals(1000.0, ds.getDomainEnd());
    assertEquals(10.0, ds.getRangeBottom());
    assertEquals(10.0, ds.getRangeTop());
    assertEquals(0.0, ds.getApproximateMinimumInterval());
  }
  
  public void testGeneral() {
    OODoubleArray domain = new OODoubleArray(new double[] {1000, 2000, 3000, 4000});
    OODoubleArray range = new OODoubleArray(new double[] {10, 50, 40, 60});
    
    XYDatasetRequest.Basic request = dsMaker.newRequest(domain.getArray(), range.getArray());
    
    // Basic test that verifies that given the same dataset values, an
    // immutable dataset and a mutable dataset (which have different code
    // paths for populating their underlying Array2D objects) produce
    // the same logical dataset state.
    
    XYDataset ds = dsFactory.create(request);
    XYDataset mutableDs = createMutableDataset(request);
    int numMipLevels = (int)MathUtil.log2(domain.size()) + 1;
    assertEqual(ds, mutableDs, numMipLevels);
  }
  
  private XYDataset createMutableDataset(XYDatasetRequest.Basic request) {
    OODoubleArray domain = new OODoubleArray(request.getDomain());
    OODoubleArray range = new OODoubleArray(request.getRange());
    request.setDomain(domain.removeLast().getArray());
    request.setRange(range.removeLast().getArray());
    
    MutableXYDataset ds = dsFactory.createMutable(request);
    ds.mutate(Mutation.append(domain.getLast(), range.getLast()));
    
    return ds;
  }
  
  private static void assertEqual(XYDataset expected, XYDataset actual, int numMipLevels) {
    assertEquals(expected.getRangeBottom(), actual.getRangeBottom());
    assertEquals(expected.getRangeTop(), actual.getRangeTop());
    assertEquals(expected.getApproximateMinimumInterval(), actual.getApproximateMinimumInterval());
    assertEquals(expected.getDomainBegin(), actual.getDomainBegin());
    assertEquals(expected.getDomainEnd(), actual.getDomainEnd());
    
    for (int i = 0; i < numMipLevels; i++) {
      assertEquals(expected.getNumSamples(i), actual.getNumSamples(i));
      int numSamples = actual.getNumSamples(i);
      for (int j = 0; j < numSamples; j++) {
        assertEquals(expected.getX(j, i), actual.getX(j, i));
        assertEquals(expected.getY(j, i), actual.getY(j, i));
      }
    }
  }
  
}
