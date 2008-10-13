package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.canvas.mock.MockView;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.gss.GssContext;

/**
 *
 */
public class MockChartPanel implements ViewReadyCallback {

  private int width;

  private int height;

  private Chart chart;

  private DefaultXYPlot plot;

  private ViewReadyCallback viewReadyCallback;

  private View view;

  private GssContext gssContext;

  public MockChartPanel(XYDataset[] ds, int width, int height) {
    this.width = width;
    this.height = height;
    
    // configure plot
    this.plot = new DefaultXYPlot(ds, true);
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
