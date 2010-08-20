package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.Fixtures;

/**
 *
 */
public class XYDatasetTest extends TestCase {

  public void testGetApproximateMinimumInterval() {
    MockXYDataset mxy = new MockXYDataset();
    assertTrue("Approximate minimum interval should be atleast less than twice as big as first two datapoints in Mock dataset",
        mxy.getApproximateMinimumInterval() <= 2 * (mxy.getX(1) - mxy.getX(0)));
    
    XYDataset xy = Fixtures.getPositiveDomainDescendingRange();
    assertTrue("Approximate minimum interval should be atleast less than twice as big as first two datapoints in Fixture dataset",
      xy.getApproximateMinimumInterval() <= 2 * (xy.getX(1) - mxy.getX(0)));
  }
}
