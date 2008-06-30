package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.browser.ChartPanel;

/**
 *
 */
public class ChronoscopeMock {

  public static MockChartPanel createTimeseriesChart(XYDataset[] ds, int width,
      int height) {
    return new MockChartPanel(ds, width, height);
  }
}
