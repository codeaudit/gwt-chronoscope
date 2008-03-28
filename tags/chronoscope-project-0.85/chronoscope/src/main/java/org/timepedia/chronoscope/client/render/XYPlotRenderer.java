package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Responsible for iterating over datasets in a defined drawing order, and then
 * deciding on how to map the visible portions of the domain onto the associated
 * xyRenderers
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class XYPlotRenderer {

  protected XYPlot plot;

  public XYPlotRenderer(XYPlot plot) {

    this.plot = plot;
  }

  /**
   * Override to implement custom scaling logic
   */
  public abstract void drawDataset(Layer can, XYDataset dataSet,
      XYRenderer xyRenderer, int seriesNum, XYPlot plot);

  public void drawDatasets() {

    int renderOrder[] = sortDatasetsIntoRenderOrder();
    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < renderOrder.length; i++) {
      int index = renderOrder[i];
      drawDataset(plotLayer, plot.getDataset(index), plot.getRenderer(index),
          index, plot);
    }
  }

  public int[] sortDatasetsIntoRenderOrder() {
    int[] order = new int[plot.getNumDatasets()];
    int d = 0;
    //  all unfocused barcharts first
    for (int i = 0; i < plot.getNumDatasets(); i++) {
      XYRenderer renderer = plot.getRenderer(i);
      if (renderer instanceof BarChartXYRenderer && (plot.getFocusPoint() == -1
          || plot.getFocusSeries() != i)) {
        order[d++] = i;
      }
    }

    // next render unfocused non-barcharts
    for (int i = 0; i < plot.getNumDatasets(); i++) {
      XYRenderer renderer = plot.getRenderer(i);

      if (!(renderer instanceof BarChartXYRenderer) && (
          plot.getFocusPoint() == -1 || plot.getFocusSeries() != i)) {
        order[d++] = i;
      }
    }

    // finally render the focused series
    if (plot.getFocusPoint() != -1 && plot.getFocusSeries() != -1) {
      int num = plot.getFocusSeries();
      order[d++] = num;
    }
    return order;
  }
}
