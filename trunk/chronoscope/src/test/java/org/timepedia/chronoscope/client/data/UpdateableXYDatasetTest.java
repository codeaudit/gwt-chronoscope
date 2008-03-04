package org.timepedia.chronoscope.client.data;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Test proper functioning of updateable datasets
 */
public class UpdateableXYDatasetTest extends GWTTestCase {

  private static final int TEST_X = 10;

  private static final int TEST_Y = 10;

  public boolean datasetChanged;

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }

  public void testInsertOperation() {
    final RangeMutableXYDataset xy = createTestDataset();
    final int oldNumSamples = xy.getNumSamples();

    datasetChanged = false;

    xy.addXYDatasetListener(new XYDatasetListener() {

      public void onDatasetChanged(double domainStart, double domainEnd) {

        assertTrue(
            "Domain Start and Domain End of datasetChanged should be " + TEST_X,
            domainStart == TEST_X && domainEnd == TEST_X);
        datasetChanged = true;
      }
    });

    xy.beginUpdate();
    xy.insertXY(TEST_X, TEST_Y);
    xy.endUpdate();

    assertTrue("dataset listener failed to invoke for insertXY operation",
        datasetChanged);

    assertTrue("NumSamples should increase by 1",
        xy.getNumSamples() == oldNumSamples + 1);

    assertTrue("Last element of dataset should have domain value of " + TEST_X
        + " and range value of " + TEST_Y,
        xy.getX(oldNumSamples) == TEST_X && xy.getY(oldNumSamples) == TEST_Y);

    // TODO: need tests for upper multiresolution levels being modified and modified
    // correctly
  }

  public void testInsertWithoutBeginUpdate() {
    RangeMutableXYDataset xy = createTestDataset();
    try {
      xy.insertXY(1, 2);
      fail(
          "Calling insertXY without matching beginUpdate should throw exception");
    } catch (Exception e) {
    }
  }

  public void testSetRangeWithoutBeginUpdate() {
    RangeMutableXYDataset xy = createTestDataset();
    try {
      xy.setY(0, 2);
      fail(
          "Calling insertXY without matching beginUpdate should throw exception");
    } catch (Exception e) {
    }
  }

  public void testSetYOperation() {
    final RangeMutableXYDataset xy = createTestDataset();
    final int oldNumSamples = xy.getNumSamples();
    final int testindex = oldNumSamples / 2;

    datasetChanged = false;

    xy.addXYDatasetListener(new XYDatasetListener() {

      public void onDatasetChanged(double domainStart, double domainEnd) {

        double domainVal = xy.getX(testindex);
        assertTrue("Domain Start and Domain End of datasetChanged should be "
            + domainVal + " not " + domainStart + " and " + domainEnd,
            domainStart == domainVal && domainEnd == domainVal);
        datasetChanged = true;
      }
    });

    xy.beginUpdate();
    xy.setY(testindex, TEST_Y);
    xy.endUpdate();

    assertTrue("dataset listener failed to invoke for setXY operation",
        datasetChanged);

    assertTrue("NumSamples should remain the same",
        xy.getNumSamples() == oldNumSamples);

    assertTrue("Element at index " + testindex + " should have Y value of "
        + TEST_Y + " not " + xy.getY(testindex), xy.getY(testindex) == TEST_Y);

    // TODO: need tests for upper multiresolution levels being modified and modified
    // correctly
  }

  public void testUnmatchedBeginUpdate() {
    RangeMutableXYDataset xy = createTestDataset();
    try {
      xy.endUpdate();
      fail(
          "Calling beginUpdate twice without matching endUpdate should throw exception");
    } catch (Exception e) {
    }
  }

  public void testUnmatchedEndUpdate() {
    RangeMutableXYDataset xy = createTestDataset();
    try {
      xy.endUpdate();
      fail(
          "Calling endUpdate without matching beginUpdate should throw exception");
    } catch (Exception e) {
    }
  }

  private RangeMutableXYDataset createTestDataset() {
    double domain[] = {1, 2, 3, 4, 5, 6, 7, 8};
    double range[] = {1, 2, 3, 4, 5, 6, 7, 8};
    RangeMutableXYDataset xy = new RangeMutableArrayXYDataset("test", domain,
        range, "test", "test");
    return xy;
  }
}
