package org.timepedia.chronoscope.client;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.data.DatasetRequestMaker;
import org.timepedia.chronoscope.client.data.DefaultXYDatasetFactory;
import org.timepedia.chronoscope.client.data.MutableXYDataset;
import org.timepedia.chronoscope.client.data.Mutation;
import org.timepedia.chronoscope.client.data.XYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetRequest;

/**
 * @author chad takahashi
 */
public class DatasetsTest extends TestCase {
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  private XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
  
  public void testAggregateCalcs() {
    MutableXYDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    Datasets grp = new Datasets();
    grp.add(mds);
    assertEquals(1.0, grp.getMinDomain());
    assertEquals(2.0, grp.getMaxDomain());
    assertEquals(10.0, grp.getMinRange());
    assertEquals(20.0, grp.getMaxRange());
    assertEquals(1.0, grp.getMinInterval());
  }
  

  public void testIndexOf() {
    XYDataset ds0 = newDataset(new double[] {1.0}, new double[] {10.0});
    XYDataset ds1 = newDataset(new double[] {1.0}, new double[] {10.0});
    XYDataset ds2 = newDataset(new double[] {1.0}, new double[] {10.0});
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
    MutableXYDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    Datasets grp = new Datasets();
    grp.add(mds);
    
    mds.mutate(Mutation.append(3.0, 30.0));
    mds.mutate(Mutation.append(4.0, 40.0));
    mds.mutate(Mutation.setY(0, -9.0));
    mds.mutate(Mutation.append(4.1, 0.1));
    
    assertEquals(1.0, grp.getMinDomain());
    assertEquals(4.1, grp.getMaxDomain());
    assertEquals(-9.0, grp.getMinRange());
    assertEquals(40.0, grp.getMaxRange());
    assertEquals(0.1, grp.getMinInterval(), 0.000000000000001);
  }
  
  public void testRemove() {
    XYDataset ds0 = newMutableDataset(new double[] {2.0, 5.0}, new double[] {10.0, 11.0});
    XYDataset ds1 = newMutableDataset(new double[] {1.0, 8.0}, new double[] {30.0, 4.0});
    XYDataset ds2 = newMutableDataset(new double[] {3.0, 7.0}, new double[] {20.0, 5.0});
    Datasets grp = new Datasets();
    grp.add(ds0);
    grp.add(ds1);
    grp.add(ds2);
    
    MutableXYDataset removedDataset = (MutableXYDataset)grp.remove(1);
    
    // verify the removed element corresponds to the index
    assertTrue(ds1 == removedDataset);
    // verify the new size of the container
    assertEquals(2, grp.size());
    // verify the order of the remaining elements
    assertTrue(ds0 == grp.get(0));
    assertTrue(ds2 == grp.get(1));
    
    // make sure aggregate properties were updated
    assertEquals(2.0, grp.getMinDomain());
    assertEquals(7.0, grp.getMaxDomain());
    assertEquals(5.0, grp.getMinRange());
    assertEquals(20.0, grp.getMaxRange());
    assertEquals(3.0, grp.getMinInterval());
    
    // verify that the container de-registered itself as a listener
    // to the removed dataset.
    removedDataset.mutate(Mutation.RangeMutation.setY(0, 999.0));
    assertEquals(20.0, grp.getMaxRange());
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
  
  private XYDataset newDataset(double[] domain, double[] range) {
    XYDatasetRequest.Basic request = dsMaker.newRequest(domain, range);
    return dsFactory.create(request);
  }

  private MutableXYDataset newMutableDataset(double[] domain, double[] range) {
    XYDatasetRequest.Basic request = dsMaker.newRequest(domain, range);
    return dsFactory.createMutable(request);
  }
}
