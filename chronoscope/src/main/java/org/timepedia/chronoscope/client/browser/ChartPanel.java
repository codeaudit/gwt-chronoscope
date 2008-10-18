package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.ScalableXYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

public class ChartPanel extends Composite implements Exportable {

  private PlotPanel plotPanel;

  public ChartPanel(Dataset[] datasetArray, int chartWidth, int chartHeight) {
    this(DOM.createDiv(), datasetArray, chartWidth, chartHeight, null);
  }

  public ChartPanel(Element elem, Dataset[] datasetArray, int chartWidth,
      int chartHeight, ViewReadyCallback readyListener) {
    
    ArgChecker.isNotNull(datasetArray, "datasetArray");
    if(elem == null) {
      elem=DOM.createDiv();
    }
    
    XYPlot plot = createPlot(datasetArray);
    plotPanel = new PlotPanel(elem, plot, chartWidth, chartHeight,
        readyListener);

    initWidget(plotPanel);
  }

  protected XYPlot createPlot(Dataset[] datasetArray) {
    
    Datasets<Tuple2D> datasets = new Datasets<Tuple2D>(datasetArray);
    
    XYPlotRenderer plotRenderer = new ScalableXYPlotRenderer();
    
    DefaultXYPlot plot = new DefaultXYPlot();
    plot.setDatasets(datasets);
    plot.setPlotRenderer(plotRenderer);
    
    return plot;
  }
  
  public void attach() {
    onAttach();
    RootPanel.detachOnWindowClose(this);
  }

  /**
   * @gwt.export
   */
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
    plotPanel.setReadyListener(viewReadyCallback);
  }

}
