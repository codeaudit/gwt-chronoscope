package org.timepedia.chronoscope.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.exporter.client.ExporterBase;

/**
 *
 */
public class TestExports extends ChronoscopeTestCase {

  public void onChronoscopeLoaded() {
    finishTest();
    assertTrue(true);
  }

  public void testOnChronoscopeLoaded() {
    setupCatchFunction();
    delayTestFinish(5000);
    Chronoscope.initialize();
  }

  private native void setupCatchFunction() /*-{
    var t=this;
    $wnd.onChronoscopeLoaded=function(chrono) {
      t.@org.timepedia.chronoscope.client.TestExports::onChronoscopeLoaded()();
    }
  }-*/;

  public void testRangeAxisExports() {
    XYDataset ds[] = new XYDataset[2];
    ds[0] = new MockXYDataset();
    ds[1] = new MockXYDataset();
    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        assertTrue(isSetAutoZoomVisibleRange(ExporterBase.wrap(view)));
        assertTrue(isSetVisibleRange(ExporterBase.wrap(view)));
        assertTrue(isSetLabel(ExporterBase.wrap(view)));
        finishTest();
      }
    });
    delayTestFinish(2000);
    RootPanel.get().add(cp);
  }

  private native boolean isSetLabel(JavaScriptObject view) /*-{
     return view.getChart().getPlot().getAxis(0).setLabel != undefined;
  }-*/;

  private native boolean isSetVisibleRange(JavaScriptObject view) /*-{
     return view.getChart().getPlot().getAxis(0).setVisibleRange != undefined;
  }-*/;

  private native boolean isSetAutoZoomVisibleRange(JavaScriptObject view)/*-{
     return view.getChart().getPlot().getAxis(0).setAutoZoomVisibleRange != undefined;
  }-*/;
}
