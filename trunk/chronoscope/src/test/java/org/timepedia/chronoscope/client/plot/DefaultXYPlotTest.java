package org.timepedia.chronoscope.client.plot;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;

/**
 * Test case for XYPlot interface
 */
public class DefaultXYPlotTest extends GWTTestCase {

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }

  /**  
   * Test that issue #23 is fixed
   * http://code.google.com/p/gwt-chronoscope/issues/detail?id=23
   */
  public void testAutoAssignDatasetAxesSameAxis() {
    
    XYDataset ds[]=new XYDataset[2];
    ds[0]=new MockXYDataset();
    ds[1]=new MockXYDataset();
    ChartPanel cp= Chronoscope.createTimeseriesChart(ds, 600, 400);
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
   * Test that issue #23 is fixed
   * http://code.google.com/p/gwt-chronoscope/issues/detail?id=23
   */
  public void testAutoAssignDatasetAxesDifferentAxis() {
    
    XYDataset ds[]=new XYDataset[2];
    ds[0]=new MockXYDataset();
    MockXYDataset mds = new MockXYDataset();
    mds.setAxisId("different");
    ds[1]= mds;
    
    ChartPanel cp= Chronoscope.createTimeseriesChart(ds, 600, 400);
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
}

