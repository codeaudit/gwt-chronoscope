package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.canvas.MockView;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.gss.GssContext;

/**
 *
 */
public class MockChartPanel implements ViewReadyCallback {

  private XYDataset[] ds;

  private int width;

  private int height;

  private Chart chart;

  private DefaultXYPlot plot;

  private ViewReadyCallback viewReadyCallback;

  private View view;

  private GssContext gssContext;

  private boolean viewReady;

  public MockChartPanel(XYDataset[] ds, int width, int height) {

    this.ds = ds;
    this.width = width;
    this.height = height;
    this.chart = new Chart();
    this.plot = new DefaultXYPlot(chart, ds, true);
    chart.setPlot(plot);
  }

  public void setViewReadyListener(ViewReadyCallback viewReadyCallback) {

    this.viewReadyCallback = viewReadyCallback;
  }

  public void onAttach() {
    this.view = new MockView();
    this.gssContext = new MockGssContext();
    view.initialize(width, height, false, gssContext, this);
    view.onAttach();
  }

  public void onViewReady(View view) {

    viewReady = true;
    chart.init(view, plot);
    if (viewReadyCallback != null) {
      viewReadyCallback.onViewReady(view);
    } else {
      chart.redraw();
    }
  }
}
