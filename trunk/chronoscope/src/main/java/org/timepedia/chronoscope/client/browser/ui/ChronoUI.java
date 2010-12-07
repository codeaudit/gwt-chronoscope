package org.timepedia.chronoscope.client.browser.ui;

import org.timepedia.chronoscope.client.browser.ChartPanel;

import com.google.gwt.user.client.ui.Composite;

/**
 * A wrapper class for ChartPanel.
 * 
 * ChartPanel can not be used directly in ui-builder because an Exporter issue which
 * makes widgets implementing Exportable fail when are used inside ui.xml files.
 * 
 * This class is a composite with a few methods to initialize the chart in a 
 * ui xml file.
 * 
 * UI Code Example
 * <pre>
   <ui:UiBinder [...] xmlns:u='urn:import:org.timepedia.chronoscope.client.browser.ui'>
   
   <u:ChronoUI ui:field="chart" datasetsVarName="__datasets" width="200" height="100"></u:ChronoUI>
 * </pre>
 */
public class ChronoUI extends Composite {
  private ChartPanel chart = new ChartPanel();

  public ChronoUI() {
    chart = new ChartPanel();
    initWidget(chart);
  }

  /**
   * Set the name of a javascript window.var which contains an array
   * of dataset, eg:
   * var datasets = [{id:..., domain: ...}, {id:..., domain: ...}]; 
   * 
   */
  public void setDatasetsVarName(String name) {
    chart.setDatasetsVarName(name);
  }

  /**
   * Set the name of a javascript window.var which contains just one 
   * dataset, eg:
   * var datasets = {id:..., domain: ...};
   * 
   * This method could be called many times before initializing the chart
   * so as you can add multiple series to the chart.
   * 
   */
  public void setDatasetVarName(String name) {
    chart.setDatasetVarName(name);
  }
  
  public void setWidth(String width) {
    chart.setWidth(width);
  }

  public void setHeight(String height) {
    chart.setHeight(height);
  }

  public ChartPanel getChart() {
    return chart;
  }
}
