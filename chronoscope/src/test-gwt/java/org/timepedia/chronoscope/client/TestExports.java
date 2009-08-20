package org.timepedia.chronoscope.client;

import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.exporter.client.ExporterUtil;

/**
 *
 */
public class TestExports extends ChronoscopeTestCaseBase {

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
    runChronoscopeTest(ds, new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        assertTrue(isSetAutoZoomVisibleRange(ExporterUtil.wrap(plot.getRangeAxis(0))));
        assertTrue(isSetVisibleRange(ExporterUtil.wrap(plot.getRangeAxis(0))));
        assertTrue(isSetLabel(ExporterUtil.wrap(plot.getRangeAxis(0))));
        finishTest();
      }
    });
  }

  private native boolean isSetLabel(JavaScriptObject axis) /*-{
     return axis.setLabel != undefined;
  }-*/;

  private native boolean isSetVisibleRange(JavaScriptObject axis) /*-{
     return axis.setVisibleRange != undefined;
  }-*/;

  private native boolean isSetAutoZoomVisibleRange(JavaScriptObject axis)/*-{
     return axis.setAutoZoomVisibleRange != undefined;
  }-*/;
}
