package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.DefaultMipMapStrategy;
import org.timepedia.chronoscope.client.data.DefaultXYDatasetFactory;
import org.timepedia.chronoscope.client.data.MutableXYDataset;
import org.timepedia.chronoscope.client.data.Mutation;
import org.timepedia.chronoscope.client.data.XYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetRequest;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.util.junit.OODoubleArray;

/**
 * @author chad takahashi
 */
public class ArrayXYDatasetTest extends TestCase {
  private XYDatasetFactory dsFactory;

  public ArrayXYDatasetTest(String name) {
    super(name);
  }
  
  public void setUp() {
    dsFactory = new DefaultXYDatasetFactory();
  }
  
  public void test() {
    OODoubleArray domain = new OODoubleArray(new double[] {1000, 2000, 3000, 4000});
    OODoubleArray range = new OODoubleArray(new double[] {10, 50, 40, 60});
    
    // Create and configure the request
    XYDatasetRequest.Basic request = new XYDatasetRequest.Basic();
    request.setAxisId("My Axis Id");
    request.setLabel("My Range Label");
    request.setDefaultMipMapStrategy(DefaultMipMapStrategy.MEAN);
    request.setDomain(domain.getArray());
    request.setRange(range.getArray());
    
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
