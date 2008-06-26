package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.data.DeferredRegionalArrayXYDataset;
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

    int numDatasets = plot.getNumDatasets();
    for (int seriesNum = 0; seriesNum < numDatasets; seriesNum++) {
      
      XYDataset dataSet = plot.getDataset(seriesNum);
      int domainStart = 0, domainEnd = 0;
      int mipLevel = -1;

      double domainOrigin = plot.getDomainOrigin();
      double currentDomain = plot.getCurrentDomain();
      if(!(dataSet.getDomainEnd() >= domainOrigin && dataSet.getDomainBegin() <= domainOrigin + currentDomain))
        continue;
      
      boolean inRegion = true;
      if (dataSet instanceof DeferredRegionalArrayXYDataset) {
        DeferredRegionalArrayXYDataset dDataset
            = (DeferredRegionalArrayXYDataset) dataSet;
        double regionStart = dDataset.getRegionBegin();
        double regionEnd = dDataset.getRegionEnd();
        inRegion = domainOrigin >= regionStart
            && domainOrigin + currentDomain <= regionEnd;
        if(!inRegion && !((DefaultXYPlot)plot).isAnimating()) {
          int regionNum = dDataset.findRegion(domainOrigin, 
              domainOrigin+currentDomain);
          if(regionNum != -1) {
            dDataset.loadRegion(regionNum);
          }
        }
      }
      int maxPoints = Math.min(
          plot.getRenderer(seriesNum).getMaxDrawableDatapoints(plot),
          plot.getMaxDrawableDataPoints());
      do {
        mipLevel++;
        double end = dataSet
            .getX(dataSet.getNumSamples(mipLevel) - 1, mipLevel);
        if (!inRegion && (domainOrigin < dataSet.getX(0, mipLevel)
            /*|| domainOrigin + currentDomain > end + (end - dataSet
            .getX(dataSet.getNumSamples(mipLevel) - 2, mipLevel))*/)) {
          continue;
        } else {
          inRegion = true;
        }
        domainStart = Util.binarySearch(dataSet, domainOrigin, mipLevel);
        domainEnd = Util
            .binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
      } while (!inRegion || (inRegion && domainEnd - domainStart > maxPoints));

      
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
        .put(plot.getRangeAxis(seriesNum).getAxisId(), new Double(rangeMin));
  }

  private void putRangeMax(int seriesNum, double rangeMax) {
    this.rangeMax
        .put(plot.getRangeAxis(seriesNum).getAxisId(), new Double(rangeMax));
  }

  private double getRangeMin(int seriesNum) {
    return ((Double) rangeMin.get(plot.getRangeAxis(seriesNum).getAxisId()))
        .doubleValue();
  }

  private double getRangeMax(int seriesNum) {
    return ((Double) rangeMax.get(plot.getRangeAxis(seriesNum).getAxisId()))
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

      rangeMin.put(ra.getAxisId(), min);
      rangeMax.put(ra.getAxisId(), max);
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
