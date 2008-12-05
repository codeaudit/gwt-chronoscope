package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.junit.OODoubleArray;

/**
 * @author chad takahashi
 */
public class ArrayDataset2DTest extends TestCase {
  private DatasetFactory dsFactory = new ChronoscopeDatasetFactory();
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  
  public void testTupleAccessors() {
    
    double[] domain = {100.0, 200.0, 300.0};
    double[] range = {1.0, 2.0, 3.0};
    DatasetRequest.Basic request = dsMaker.newRequest(domain, range);
    Dataset ds = dsFactory.create(request);
    
    for (int i = 0; i < ds.getNumSamples(); i++) {
      assertEquals(domain[i], ds.getFlyweightTuple(i).getFirst());
      assertEquals(range[i], ds.getFlyweightTuple(i).getSecond());
    }
  }
  
  public void testSinglePoint() {
    DatasetRequest request = dsMaker.newRequest(new double[] {1000}, new double[] {10});
    Dataset ds = dsFactory.create(request);

    assertEquals(1, ds.getNumSamples());
    assertEquals(new Interval(1000, 1000), ds.getExtrema(0));
    assertEquals(new Interval(10, 10), ds.getExtrema(1));
    assertEquals(0.0, ds.getMinInterval());
  }
  
  public void testGeneral() {
    OODoubleArray domain = new OODoubleArray(new double[] {1000, 2000, 3000, 4000});
    OODoubleArray range = new OODoubleArray(new double[] {10, 50, 40, 60});
    
    DatasetRequest.Basic request = dsMaker.newRequest(domain.getArray(), range.getArray());
    
    // Basic test that verifies that given the same dataset values, an
    // immutable dataset and a mutable dataset (which have different code
    // paths for populating their underlying Array2D objects) produce
    // the same logical dataset state.
    
    Dataset ds = dsFactory.create(request);
    Dataset mutableDs = createMutableDataset(request);
    int numMipLevels = (int)MathUtil.log2(domain.size()) + 1;
    assertEqual(ds, mutableDs, numMipLevels);
  }
  
  private Dataset createMutableDataset(DatasetRequest.Basic request) {
    OODoubleArray domain = new OODoubleArray(request.getTupleSlice(0));
    OODoubleArray range = new OODoubleArray(request.getTupleSlice(1));
    request.setTupleSlice(0, domain.removeLast().getArray());
    request.setTupleSlice(1, range.removeLast().getArray());
    
    MutableDataset ds = dsFactory.createMutable(request);
    ds.mutate(Mutation.append(domain.getLast(), range.getLast()));
    
    return ds;
  }
  
  private static void assertEqual(Dataset expected, Dataset actual, int numMipLevels) {
    assertEquals(expected.getExtrema(0), actual.getExtrema(0));
    assertEquals(expected.getExtrema(1), actual.getExtrema(1));
    assertEquals(expected.getMinInterval(), actual.getMinInterval());
    
    for (int i = 0; i < numMipLevels; i++) {
      assertEquals(expected.getNumSamples(i), actual.getNumSamples(i));
      int numSamples = actual.getNumSamples(i);
      for (int j = 0; j < numSamples; j++) {
        assertEquals(expected.getX(j, i), actual.getX(j, i));
        
        assertEquals(expected.getFlyweightTuple(j, i).getSecond(), 
                     actual.getFlyweightTuple(j, i).getSecond());
      }
    }
  }
  
}
