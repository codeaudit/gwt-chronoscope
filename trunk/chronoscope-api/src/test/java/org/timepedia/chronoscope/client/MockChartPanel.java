package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.canvas.mock.MockView;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssContext;

/**
 *
 */
public class MockChartPanel implements ViewReadyCallback {

  private int width;

  private int height;

  private Chart chart;

  private XYPlot plot;

  private ViewReadyCallback viewReadyCallback;

  private View view;

  private GssContext gssContext;

  public MockChartPanel(XYPlot plot, int width, int height) {
    this.width = width;
    this.height = height;
    
    // configure plot
    this.plot = plot;
    this.plot.init(view);
    
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
    // configure chart
    this.chart = new Chart();
    chart.setPlot(plot);
    chart.setView(view);
    chart.init();
    
    if (viewReadyCallback != null) {
      viewReadyCallback.onViewReady(view);
    } else {
      chart.redraw();
    }
  }
}
