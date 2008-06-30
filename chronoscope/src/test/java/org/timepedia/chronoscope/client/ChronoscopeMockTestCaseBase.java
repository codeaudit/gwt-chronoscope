package org.timepedia.chronoscope.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;

/**
 * Auto-inject Chronoscope.css for testing
 */
public class ChronoscopeMockTestCaseBase extends TestCase {

  private boolean injected;

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeMockTestSuite";
  }
  
  protected void runChronoscopeTest(XYDataset ds[], ViewReadyCallback viewReadyCallback) {
    MockChartPanel cp = ChronoscopeMock.createTimeseriesChart(ds, 600, 400);
    cp.setViewReadyListener(viewReadyCallback);
    cp.onAttach();
  }
  
  public void finishTest() { }
}