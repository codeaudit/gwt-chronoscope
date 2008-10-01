package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYDatasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for iterating over datasets in a defined drawing order, and then
 * deciding on how to map the visible portions of the domain onto the associated
 * xyRenderers.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class XYPlotRenderer {

  protected int domainStart[], domainEnd[];

  protected XYPlot plot;

  protected Map<String,Double> rangeMax = new HashMap<String,Double>();

  protected Map<String,Double> rangeMin = new HashMap<String,Double>();

  public XYPlotRenderer(XYPlot plot) {

    this.plot = plot;
  }

  public void computeVisibleDomainAndRange() {
    initArrays();

    XYDatasets datasets = plot.getDatasets();
    final int numDatasets = datasets.size();
    for (int seriesNum = 0; seriesNum < numDatasets; seriesNum++) {

      XYDataset dataSet = datasets.get(seriesNum);

      final double domainOrigin = plot.getDomainOrigin();
      final double currentDomain = plot.getCurrentDomain();
      if (!(MathUtil.isBounded(domainOrigin, dataSet.getDomainBegin()
          - currentDomain, dataSet.getDomainEnd()))) {
        continue;
      }

      int maxPoints = Math.min(
          plot.getRenderer(seriesNum).getMaxDrawableDatapoints(plot),
          plot.getMaxDrawableDataPoints());

      int domainStart = 0;
      int domainEnd = 0;
      int mipLevel = -1;
      do {
        mipLevel++;
        domainStart = Util.binarySearch(dataSet, domainOrigin, mipLevel);
        domainEnd = Util.binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
      } while (domainEnd - domainStart > maxPoints);

      plot.setCurrentMipLevel(seriesNum, mipLevel);

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

  /**
   * Override to implement custom scaling logic.
   */
  public abstract void drawDataset(int datasetIndex, Layer can, XYPlot plot);

  public void drawDatasets() {
    computeVisibleDomainAndRange();
    setupRangeAxisVisibleRanges();

    int renderOrder[] = sortDatasetsIntoRenderOrder();
    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < renderOrder.length; i++) {
      int datasetIndex = renderOrder[i];
      drawDataset(datasetIndex, plotLayer, plot);
    }
  }

  private double getRangeMax(int seriesNum) {
    return ((Double) rangeMax.get(plot.getRangeAxis(seriesNum).getAxisId())).doubleValue();
  }

  private double getRangeMin(int seriesNum) {
    return ((Double) rangeMin.get(plot.getRangeAxis(seriesNum).getAxisId())).doubleValue();
  }

  private void initArrays() {
    int numDatasets = plot.getDatasets().size();
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

      rangeMin.put(ra.getAxisId(), min);
      rangeMax.put(ra.getAxisId(), max);
    }
  }

  private void putRangeMax(int seriesNum, double rangeMax) {
    this.rangeMax.put(plot.getRangeAxis(seriesNum).getAxisId(), new Double(
        rangeMax));
  }

  private void putRangeMin(int seriesNum, double rangeMin) {
    this.rangeMin.put(plot.getRangeAxis(seriesNum).getAxisId(), new Double(
        rangeMin));
  }

  private void setupRangeAxisVisibleRanges() {
    for (int i = 0; i < plot.getDatasets().size(); i++) {
      RangeAxis ra = plot.getRangeAxis(i);
      ra.setVisibleRange(getRangeMin(i), getRangeMax(i));
    }
  }

  private int[] sortDatasetsIntoRenderOrder() {
    final int numDatasets = plot.getDatasets().size();
    int[] order = new int[numDatasets];
    int d = 0;

    Focus focus = plot.getFocus();

    //  all unfocused barcharts first
    for (int i = 0; i < numDatasets; i++) {
      XYRenderer renderer = plot.getRenderer(i);
      if (renderer instanceof BarChartXYRenderer
          && (focus == null || focus.getDatasetIndex() != i)) {
        order[d++] = i;
      }
    }

    // next render unfocused non-barcharts
    for (int i = 0; i < numDatasets; i++) {
      XYRenderer renderer = plot.getRenderer(i);

      if (!(renderer instanceof BarChartXYRenderer)
          && (focus == null || focus.getDatasetIndex() != i)) {
        order[d++] = i;
      }
    }

    // finally render the focused series
    if (focus != null) {
      int num = plot.getFocus().getDatasetIndex();
      order[d++] = num;
    }
    return order;
  }
}
