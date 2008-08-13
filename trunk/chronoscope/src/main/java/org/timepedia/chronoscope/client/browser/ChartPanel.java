package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

public class ChartPanel extends Composite implements Exportable {

  private PlotPanel plotPanel;

  public ChartPanel(XYDataset[] datasets, int chartWidth, int chartHeight) {
    this(DOM.createDiv(), datasets, chartWidth, chartHeight, null);
  }

  public ChartPanel(Element elem, XYDataset[] datasets, int chartWidth,
      int chartHeight, ViewReadyCallback readyListener) {
    plotPanel = createPlotPanel(elem, datasets, chartWidth, chartHeight,
        readyListener);
    initWidget(plotPanel);
  }

  protected PlotPanel createPlotPanel(Element elem, XYDataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    return new PlotPanel(elem, datasets, chartWidth, chartHeight,
        readyListener);
  }

  public void setGssContext(GssContext gssContext) {
    plotPanel.setGssContext(gssContext);
  }

  public void setReadyListener(ViewReadyCallback viewReadyCallback) {
    plotPanel.setReadyListener(viewReadyCallback);
  }

  /**
   * @gwt.export
   */
  @Export
  public Chart getChart() {
    return plotPanel.getChart();
  }
}
