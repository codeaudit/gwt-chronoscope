package org.timepedia.chronoscope.client;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.DatasetRequest;
import org.timepedia.chronoscope.client.data.DatasetRequestMaker;
import org.timepedia.chronoscope.client.data.ChronoscopeDatasetFactory;
import org.timepedia.chronoscope.client.data.MutableDatasetND;
import org.timepedia.chronoscope.client.data.Mutation;
import org.timepedia.chronoscope.client.util.Interval;

/**
 * @author chad takahashi
 */
public class DatasetsTest extends TestCase {
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  private DatasetFactory dsFactory = new ChronoscopeDatasetFactory();
  
  public void testAggregateCalcs() {
    MutableDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    Datasets grp = new Datasets();
    grp.add(mds);
    assertEquals(new Interval(1.0, 2.0), grp.getDomainExtrema());
    assertEquals(1.0, grp.getMinInterval());
  }
  

  public void testIndexOf() {
    Dataset ds0 = newDataset(new double[] {1.0}, new double[] {10.0});
    Dataset ds1 = newDataset(new double[] {1.0}, new double[] {10.0});
    Dataset ds2 = newDataset(new double[] {1.0}, new double[] {10.0});
    Datasets grp = new Datasets();
    grp.add(ds0);
    grp.add(ds1);
    grp.add(ds2);
    
    assertEquals(0, grp.indexOf(ds0));
    assertEquals(1, grp.indexOf(ds1));
    assertEquals(2, grp.indexOf(ds2));
    assertEquals(-1, grp.indexOf(newDataset(new double[] {1.0}, new double[] {10.0})));
  }
  
  public void testIsEmpty() {
    Datasets grp = new Datasets();
    assertTrue(grp.isEmpty());
    grp.add(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertFalse(grp.isEmpty());
    grp.remove(0);
    assertTrue(grp.isEmpty());
  }
  
  public void testMutableDataset() {
    MutableDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    Datasets grp = new Datasets();
    grp.add(mds);
    
    mds.mutate(Mutation.append(3.0, 30.0));
    mds.mutate(Mutation.append(4.0, 40.0));
    mds.mutate(Mutation.setY(0, -9.0));
    mds.mutate(Mutation.append(4.1, 0.1));
    
    assertEquals(new Interval(1.0, 4.1), grp.getDomainExtrema());
    assertEquals(0.1, grp.getMinInterval(), 0.000000000000001);
  }
  
  public void testRemove() {
    Dataset ds0 = newMutableDataset(new double[] {2.0, 5.0}, new double[] {10.0, 11.0});
    Dataset ds1 = newMutableDataset(new double[] {1.0, 8.0}, new double[] {30.0, 4.0});
    Dataset ds2 = newMutableDataset(new double[] {3.0, 7.0}, new double[] {20.0, 5.0});
    Datasets grp = new Datasets();
    grp.add(ds0);
    grp.add(ds1);
    grp.add(ds2);
    
    MutableDatasetND removedDataset = (MutableDatasetND)grp.remove(1);
    
    // verify the removed element corresponds to the index
    assertTrue(ds1 == removedDataset);
    // verify the new size of the container
    assertEquals(2, grp.size());
    // verify the order of the remaining elements
    assertTrue(ds0 == grp.get(0));
    assertTrue(ds2 == grp.get(1));
    
    // make sure aggregate properties were updated
    assertEquals(new Interval(2.0, 7.0), grp.getDomainExtrema());
    assertEquals(3.0, grp.getMinInterval());
    
    // verify that the container de-registered itself as a listener
    // to the removed dataset.
    //removedDataset.mutate(Mutation.RangeMutation.setY(0, 999.0));
    //assertEquals(20.0, grp.getMaxRange());
  }
  
  public void testSize() {
    Datasets grp = new Datasets();
    assertEquals(0, grp.size());
    grp.add(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertEquals(1, grp.size());
    grp.add(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertEquals(2, grp.size());
    grp.add(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertEquals(3, grp.size());
  }
  
  private Dataset newDataset(double[] domain, double[] range) {
    DatasetRequest.Basic request = dsMaker.newRequest(domain, range);
    return dsFactory.create(request);
  }

  private MutableDataset newMutableDataset(double[] domain, double[] range) {
    DatasetRequest.Basic request = dsMaker.newRequest(domain, range);
    return dsFactory.createMutable(request);
  }
}
