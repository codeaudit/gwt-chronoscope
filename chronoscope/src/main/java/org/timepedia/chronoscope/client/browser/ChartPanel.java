package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

@ExportPackage("chronoscope")
public class ChartPanel extends Composite implements Exportable {
  private Element domElement;
  private PlotPanel plotPanel;
  private Dataset[] datasets;
  private ViewReadyCallback viewReadyCallback;
  private int width = 400, height = 250;
  private DefaultXYPlot plot;

  public final void init() {
    ArgChecker.isNotNull(this.datasets, "this.datasets");
    ArgChecker.isNotNull(this.domElement, "this.domElement");
    
    createPlot(datasets);
    plotPanel = new PlotPanel(domElement, plot, width, height, viewReadyCallback);

    initWidget(plotPanel);
  }
  
  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  /**
   * Replace the datasets and redraw all the elements in the chart.
   * It is similar to re-create the graph but the performance is better especially 
   * with flash canvas.
   *  
   * @param datasets 
   *         array of the new datasets
   */
  @Export
  public void replaceDatasets(Dataset[] datasets) {
    this.datasets = datasets;
  }
  
  public void changeDatasets(Dataset[] datasets) {
    this.datasets = datasets;
    System.out.println("A");
    plot.setDatasets(new Datasets<Tuple2D>(datasets));
    System.out.println("B");
    plot.init();
    System.out.println("C");
    plot.redraw();
    System.out.println("D");
  }
  
  public void setDomElement(Element element) {
    this.domElement = element;
  }
  
  public void setViewReadyCallback(ViewReadyCallback callback) {
    setReadyListener(callback);
  }
  
  protected XYPlot createPlot(Dataset[] datasetArray) {
    plot = new DefaultXYPlot();
    plot.setDatasets(new Datasets<Tuple2D>(datasetArray));
    plot.setPlotRenderer(new XYPlotRenderer());
    return plot;
  }

  public void attach() {
    onAttach();
    RootPanel.detachOnWindowClose(this);
  }

  @Export
  public void detach() {
    onDetach();
    if (getElement() != null) {
      try {
        getElement().getParentElement().removeChild(getElement());
      } catch (Exception e) {
        GWT.log("Can't detach " + e, e);
      }
    }
  }

  @Export
  public Chart getChart() {
    return plotPanel.getChart();
  }

  public int getChartHeight() {
    return plotPanel.getChartHeight();
  }

  public int getChartWidth() {
    return plotPanel.getChartWidth();
  }

  public void setGssContext(GssContext gssContext) {
    plotPanel.setGssContext(gssContext);
  }

  public void setReadyListener(ViewReadyCallback viewReadyCallback) {
    if(plotPanel == null) {
      this.viewReadyCallback = viewReadyCallback;
    }
    else {
      plotPanel.setReadyListener(viewReadyCallback);
    }
  }

}
