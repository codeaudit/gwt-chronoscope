package org.timepedia.chronoscope.java2d;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.java2d.canvas.CanvasJava2D;
import org.timepedia.chronoscope.java2d.canvas.ViewJava2D;

import java.awt.Image;

/**
 * Class used to create static images for server side rendering
 */
public class StaticImageChartPanel implements ViewReadyCallback {

  private Chart chart;

  private DefaultXYPlot plot;

  private ViewJava2D view;

  public StaticImageChartPanel(XYDataset[] datasets, int width, int height,
      GssContext gssContext) {
    chart = new Chart();
    plot = new DefaultXYPlot(chart, datasets, false);
    chart.setPlot(plot);

    view = new ViewJava2D();
    view.initialize(width, height, false, gssContext, this);
    view.onAttach();
  }

  public StaticImageChartPanel(XYDataset[] datasets, int width, int height) {
    this(datasets, width, height, new MockGssContext());
  }

  public Chart getChart() {
    return chart;
  }

  public Image getImage() {
    return ((CanvasJava2D) view.getCanvas()).getImage();
  }

  public void onViewReady(View view) {
    chart.init(view, plot);
    chart.redraw();
  }

  public void redraw() {
    chart.redraw();
  }
}
