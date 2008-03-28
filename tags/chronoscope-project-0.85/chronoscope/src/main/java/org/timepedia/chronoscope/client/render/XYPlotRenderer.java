package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.Util;

import java.util.HashMap;

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

  protected int domainStart[], domainEnd[];

  protected HashMap rangeMin = new HashMap();

  protected HashMap rangeMax = new HashMap();

  public void computeVisibleDomainAndRange() {
    initArrays();

    for (int seriesNum = 0; seriesNum < plot.getNumDatasets(); seriesNum++) {
      XYDataset dataSet = plot.getDataset(seriesNum);
      int domainStart, domainEnd;
      int mipLevel = -1;

      double domainOrigin = plot.getDomainOrigin();
      double currentDomain = plot.getCurrentDomain();
      do {
        mipLevel++;

        domainStart = Util.binarySearch(dataSet, domainOrigin, mipLevel);
        domainEnd = Util
            .binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
      } while (domainEnd - domainStart > Math.min(
          plot.getRenderer(seriesNum).getMaxDrawableDatapoints(plot),
          plot.getMaxDrawableDataPoints()));

      plot.setCurrentDatasetLevel(seriesNum, mipLevel);

      this.domainStart[seriesNum] = domainStart;
      this.domainEnd[seriesNum] = domainEnd;

      double rangeMin = getRangeMin(seriesNum);
      double rangeMax = getRangeMax(seriesNum);

      for (int i = domainStart; i <= domainEnd; i++) {
        double val = dataSet.getY(i, mipLevel);
        rangeMin = Math.min(rangeMin, val);
        rangeMax = Math.max(rangeMax, val);
      }
      putRangeMin(seriesNum, rangeMin);
      putRangeMax(seriesNum, rangeMax);
    }
  }

  private void putRangeMin(int seriesNum, double rangeMin) {
    this.rangeMin
        .put(plot.getRangeAxis(seriesNum).getUnitLabel(), new Double(rangeMin));
  }

  private void putRangeMax(int seriesNum, double rangeMax) {
    this.rangeMax
        .put(plot.getRangeAxis(seriesNum).getUnitLabel(), new Double(rangeMax));
  }

  private double getRangeMin(int seriesNum) {
    return ((Double) rangeMin.get(plot.getRangeAxis(seriesNum).getUnitLabel()))
        .doubleValue();
  }

  private double getRangeMax(int seriesNum) {
    return ((Double) rangeMax.get(plot.getRangeAxis(seriesNum).getUnitLabel()))
        .doubleValue();
  }

  private void initArrays() {
    int numDatasets = plot.getNumDatasets();
    if (domainStart == null || domainStart.length != numDatasets) {
      domainStart = new int[numDatasets];
      domainEnd = new int[numDatasets];
    }
    int numAxes = plot.getRangeAxisCount();
    if (rangeMin.size() != numAxes) {
      rangeMin.clear();
      rangeMax.clear();
    }
    Double min = new Double(Double.POSITIVE_INFINITY);
    Double max = new Double(Double.NEGATIVE_INFINITY);

    for (int i = 0; i < numDatasets; i++) {
      RangeAxis ra = plot.getRangeAxis(i);

      rangeMin.put(ra.getUnitLabel(), min);
      rangeMax.put(ra.getUnitLabel(), max);
    }
  }

  /**
   * Override to implement custom scaling logic
   */
  public abstract void drawDataset(Layer can, XYDataset dataSet,
      XYRenderer xyRenderer, int seriesNum, XYPlot plot);

  public void drawDatasets() {

    computeVisibleDomainAndRange();
    setupRangeAxisVisibleRanges();

    int renderOrder[] = sortDatasetsIntoRenderOrder();
    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < renderOrder.length; i++) {
      int index = renderOrder[i];
      drawDataset(plotLayer, plot.getDataset(index), plot.getRenderer(index),
          index, plot);
    }
  }

  private void setupRangeAxisVisibleRanges() {
    for (int i = 0; i < plot.getNumDatasets(); i++) {
      RangeAxis ra = plot.getRangeAxis(i);
      ra.setVisibleRange(getRangeMin(i), getRangeMax(i));
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
