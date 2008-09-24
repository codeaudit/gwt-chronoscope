package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

public class ChartPanel extends Composite implements Exportable {

  protected PlotPanel plotPanel;

  public ChartPanel(XYDataset[] datasets, int chartWidth, int chartHeight) {
    this(DOM.createDiv(), datasets, chartWidth, chartHeight, null);
  }

  public ChartPanel(Element elem, XYDataset[] datasets, int chartWidth,
      int chartHeight, ViewReadyCallback readyListener) {
    plotPanel = createPlotPanel(elem, datasets, chartWidth, chartHeight,
        readyListener);

    initWidget(plotPanel);
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

  protected PlotPanel createPlotPanel(Element elem, XYDataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    if(elem == null) elem=DOM.createDiv();
    return new PlotPanel(elem, datasets, chartWidth, chartHeight,
        readyListener);
  }
}
