package com.example.client;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloChart implements EntryPoint {

  public void onModuleLoad() {
    double GOLDEN_RATIO = 1.618;
    int chartWidth = 600, chartHeight = (int) (chartWidth / GOLDEN_RATIO);
    Chronoscope.setFontBookRendering(false);
    Chronoscope.initialize();
    Chronoscope chronoscope = Chronoscope.getInstance();


    Dataset[] dataset = new Dataset[1];
    dataset[0] = chronoscope.createDataset(getJson("data1"));
    VerticalPanel vp = new VerticalPanel();
    vp.add(new Label(dataset[0].getRangeLabel()));
    ChartPanel chartPanel = Chronoscope.createTimeseriesChart(dataset,
        chartWidth, chartHeight);
    vp.add(chartPanel);
    RootPanel.get().add(vp);
  }

  private static native JsonDatasetJSO getJson(String varName) /*-{
    return $wnd[varName];
  }-*/;
}
