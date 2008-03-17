package org.timepedia.chronoscope.client.plot;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Fixtures;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.MockXYDataset;

/**
 * Test case for XYPlot interface
 */
public class DefaultXYPlotTest extends GWTTestCase {

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }

  /**
   * Test that issue #23 is fixed http://code.google.com/p/gwt-chronoscope/issues/detail?id=23
   */
  public void testAutoAssignDatasetAxesSameAxis() {

    XYDataset ds[] = new XYDataset[2];
    ds[0] = new MockXYDataset();
    ds[1] = new MockXYDataset();
    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        assertSame(plot.getRangeAxis(0), plot.getRangeAxis(1));
        finishTest();
      }
    });
    delayTestFinish(2000);
    RootPanel.get().add(cp);
  }

  /**
   * Test that issue #23 is fixed http://code.google.com/p/gwt-chronoscope/issues/detail?id=23
   */
  public void testAutoAssignDatasetAxesDifferentAxis() {

    XYDataset ds[] = new XYDataset[2];
    ds[0] = new MockXYDataset();
    MockXYDataset mds = new MockXYDataset();
    mds.setAxisId("different");
    ds[1] = mds;

    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        assertNotSame(plot.getRangeAxis(0), plot.getRangeAxis(1));
        finishTest();
      }
    });
    delayTestFinish(2000);
    RootPanel.get().add(cp);
  }

  /**
   * Test fix for issue #28
   */
  public void testComputeDomainMinMax() {
    final XYDataset ds[] = new XYDataset[2];
    ds[0] = Fixtures.getNegativeDomainAscendingRange();
    ds[1] = Fixtures.getPositiveDomainDescendingRange();

    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        assertEquals(ds[0].getX(0), plot.getDomainMin(), 0.0);
        assertEquals(ds[1].getX(ds[1].getNumSamples() - 1),
            plot.getDomainMax(), 0.0);
        finishTest();
      }
    });
    delayTestFinish(2000);
    RootPanel.get().add(cp);
  }
}

