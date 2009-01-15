package org.timepedia.chronoscope.java2d;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
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

  private boolean interactive;

  public StaticImageChartPanel(Dataset[] datasets, int width, int height,
      GssContext gssContext) {
    this(datasets, true, width, height, gssContext);
  }

  public StaticImageChartPanel(Dataset[] datasets, int width, int height) {
    this(datasets, width, height, new MockGssContext());
  }

  public StaticImageChartPanel(Dataset[] ds, boolean interactive, int width,
      int height) {
    this(ds, interactive, width, height, new MockGssContext());
  }

  public StaticImageChartPanel(Dataset[] ds, boolean interactive, int width,
      int height, GssContext gssContext) {
    this.interactive = interactive;
    chart = new Chart();
    plot = new DefaultXYPlot();
    Datasets dss = new Datasets(ds);

    plot.setDatasets(dss);
    plot.setOverviewEnabled(false);
    plot.setPlotRenderer(new XYPlotRenderer());
    chart.setPlot(plot);

    view = new ViewJava2D();
    chart.setView(view);
    view.initialize(width, height, false, gssContext, this);
    view.onAttach();
  }

  public Chart getChart() {
    return chart;
  }

  public Image getImage() {
    return ((CanvasJava2D) view.getCanvas()).getImage();
  }

  public void onViewReady(View view) {
    plot.init(view);
    chart.setView(view);
    chart.init();
    if (!interactive) {
      plot.setSubPanelsEnabled(false);
    }
    plot.reloadStyles();
  }

  public void redraw() {
    chart.redraw();
  }
}
