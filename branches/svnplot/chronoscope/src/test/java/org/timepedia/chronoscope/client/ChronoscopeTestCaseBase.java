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

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuiteMock";
  }

  protected void runChronoscopeTest(XYDataset ds[],
      ViewReadyCallback viewReadyCallback) {
    Chronoscope.setErrorReporting(false);
    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(viewReadyCallback);
    delayTestFinish(60000);
    RootPanel.get().add(cp);
  }
}