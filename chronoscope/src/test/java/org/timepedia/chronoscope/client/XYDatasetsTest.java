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
public class XYDatasetsTest extends TestCase {
  private DatasetRequestMaker dsMaker = new DatasetRequestMaker();
  private XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
  
  public void testAggregateCalcs() {
    MutableXYDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    XYDatasets grp = new XYDatasets(mds);
    assertEquals(1.0, grp.getMinDomain());
    assertEquals(2.0, grp.getMaxDomain());
    assertEquals(10.0, grp.getMinRange());
    assertEquals(20.0, grp.getMaxRange());
  }
  

  public void testIndexOf() {
    XYDataset ds0 = newDataset(new double[] {1.0}, new double[] {10.0});
    XYDataset ds1 = newDataset(new double[] {1.0}, new double[] {10.0});
    XYDataset ds2 = newDataset(new double[] {1.0}, new double[] {10.0});
    XYDatasets grp = new XYDatasets(ds0);
    grp.addDataset(ds1);
    grp.addDataset(ds2);
    
    assertEquals(0, grp.indexOf(ds0));
    assertEquals(1, grp.indexOf(ds1));
    assertEquals(2, grp.indexOf(ds2));
    assertEquals(-1, grp.indexOf(newDataset(new double[] {1.0}, new double[] {10.0})));
  }
  
  public void testMutableDataset() {
    MutableXYDataset mds = newMutableDataset(new double[] {1.0, 2.0}, new double[] {10.0, 20.0});
    XYDatasets grp = new XYDatasets(mds);
    
    mds.mutate(Mutation.append(3.0, 30.0));
    mds.mutate(Mutation.append(4.0, 40.0));
    mds.mutate(Mutation.setY(0, -9.0));
    mds.mutate(Mutation.append(5.0, 0.1));
    
    assertEquals(1.0, grp.getMinDomain());
    assertEquals(5.0, grp.getMaxDomain());
    assertEquals(-9.0, grp.getMinRange());
    assertEquals(40.0, grp.getMaxRange());
  }
  
  public void testSize() {
    XYDatasets grp = new XYDatasets(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertEquals(1, grp.size());
    grp.addDataset(newDataset(new double[] {1.0}, new double[] {10.0}));
    assertEquals(2, grp.size());
    grp.addDataset(newDataset(new double[] {1.0}, new double[] {10.0}));
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
