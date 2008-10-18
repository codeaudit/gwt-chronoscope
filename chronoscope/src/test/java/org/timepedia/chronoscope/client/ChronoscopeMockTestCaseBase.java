package org.timepedia.chronoscope.client;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

/**
 * Auto-inject Chronoscope.css for testing
 */
public class ChronoscopeMockTestCaseBase extends TestCase {

  private boolean injected;

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeMockTestSuite";
  }
  
  protected void runChronoscopeTest(Dataset ds[], ViewReadyCallback viewReadyCallback) {
    MockChartPanel cp = ChronoscopeMock.createTimeseriesChart(ds, 600, 400);
    cp.setViewReadyListener(viewReadyCallback);
    cp.onAttach();
  }
  
  public void finishTest() { }
}