package org.timepedia.chronoscope.client;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.junit.client.GWTTestCase;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

/**
 * Auto-inject Chronoscope.css for testing
 */
public class ChronoscopeTestCaseBase extends GWTTestCase {

  private boolean injected;

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuiteMock";
  }

  private static native Element getHead() /*-{
      return $doc.getElementsByTagName("head")[0];
    }-*/;

  protected void runChronoscopeTest(XYDataset ds[],
      ViewReadyCallback viewReadyCallback) {
    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(viewReadyCallback);
    delayTestFinish(60000);
    RootPanel.get().add(cp);
  }
}