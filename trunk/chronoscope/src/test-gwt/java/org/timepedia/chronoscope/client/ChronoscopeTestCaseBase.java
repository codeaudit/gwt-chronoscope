package org.timepedia.chronoscope.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;

/**
 * Auto-inject Chronoscope.css for testing
 */
public class ChronoscopeTestCaseBase extends GWTTestCase {

  private boolean injected;

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }
  
  private static native Element getHead() /*-{
    return $doc.getElementsByTagName("head")[0];
  }-*/;

   
  protected void runChronoscopeTest(XYDataset ds[], ViewReadyCallback viewReadyCallback) {
    ChartPanel cp = Chronoscope.createTimeseriesChart(ds, 600, 400);
    cp.setReadyListener(viewReadyCallback);
    delayTestFinish(15000);
    RootPanel.get().add(cp);
  }
}
