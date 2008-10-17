package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.data.tuple.Tuple;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.Util;

/**
 * Responsible for iterating over {@link Dataset}s in a defined drawing order, 
 * and then deciding on how to map the visible portions of the domain onto the 
 * associated dataset renderers.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class XYPlotRenderer<S extends Tuple, T extends Dataset<S>> {

  // For each dataset, stores the start and end data point indices that are
  // currently visible in the plot.
  // For example, domainStartIdxs[2] stores the leftmost data point index of the
  // 3rd dataset in plot.datsets that's currently viewable in the plot.
  protected int domainStartIdxs[], domainEndIdxs[];
  
  // Stores the order in which the datasets should be rendered.  For example, if
  // element [0] = 4, then this means the 5th dataset should be rendered first.
  private int[] datasetRenderOrder;
  
  // Stores the min and max range values for a given domain interval for each dataset.
  // For example, minRanges[2] stores the minimum range value (within the current plot
  // domain interval) for the 3rd dataset in plot.datsets.
  private double[] minRanges, maxRanges;
  
  private XYPlot<S,T> plot;
  
  private void computeVisibleDomainAndRange() {
    Datasets<S,T> datasets = plot.getDatasets();
    final int numDatasets = datasets.size();
    
    for (int datasetIdx = 0; datasetIdx < numDatasets; datasetIdx++) {
      // FIXME: refactor to get rid of cast
      XYDataset dataSet = (XYDataset)datasets.get(datasetIdx);

      final double plotDomainStart = plot.getDomain().getStart();
      final double plotDomainLength = plot.getDomain().length();
      if (!(MathUtil.isBounded(plotDomainStart, dataSet.getDomainBegin()
          - plotDomainLength, dataSet.getDomainEnd()))) {
        continue;
      }

      final int maxPoints = Math.min(
          plot.getRenderer(datasetIdx).getMaxDrawableDatapoints(plot),
          plot.getMaxDrawableDataPoints());

      int domainStartIdx = 0;
      int domainEndIdx = 0;
      int mipLevel = -1;
      do {
        mipLevel++;
        domainStartIdx = Util.binarySearch(dataSet, plotDomainStart, mipLevel);
        domainEndIdx = Util.binarySearch(dataSet, plotDomainStart + plotDomainLength, mipLevel);
      } while (domainEndIdx - domainStartIdx > maxPoints);

      plot.setCurrentMipLevel(datasetIdx, mipLevel);
      this.domainStartIdxs[datasetIdx] = domainStartIdx;
      this.domainEndIdxs[datasetIdx] = domainEndIdx;

      double rangeMin = Double.POSITIVE_INFINITY;
      double rangeMax = Double.NEGATIVE_INFINITY;
      for (int i = domainStartIdx; i <= domainEndIdx; i++) {
        double val = dataSet.getY(i, mipLevel);
        rangeMin = Math.min(rangeMin, val);
        rangeMax = Math.max(rangeMax, val);
      }
      minRanges[datasetIdx] = rangeMin;
      maxRanges[datasetIdx] = rangeMax;
    }
  }

  /**
   * Override to implement custom scaling logic.
   */
  public abstract void drawDataset(int datasetIndex, Layer layer, XYPlot<S,T> plot);

  public void drawDatasets() {
    final int numDatasets = plot.getDatasets().size();
    
    allocateArrayLengths(numDatasets);
    computeVisibleDomainAndRange();
    setupRangeAxisVisibleRanges(numDatasets);
    sortDatasetsIntoRenderOrder(datasetRenderOrder);

    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < numDatasets; i++) {
      drawDataset(datasetRenderOrder[i], plotLayer, plot);
    }
  }

  public void setPlot(XYPlot<S,T> plot) {
    this.plot = plot;
  }
  
  private void allocateArrayLengths(int numDatasets) {
    if (domainStartIdxs == null || domainStartIdxs.length != numDatasets) {
      datasetRenderOrder = new int[numDatasets];
      domainStartIdxs = new int[numDatasets];
      domainEndIdxs = new int[numDatasets];
      minRanges = new double[numDatasets];
      maxRanges = new double[numDatasets];
    }
  }

  private void setupRangeAxisVisibleRanges(int numDatasets) {
    for (int i = 0; i < numDatasets; i++) {
      RangeAxis ra = plot.getRangeAxis(i);
      ra.setVisibleRange(minRanges[i], maxRanges[i]);
    }
  }

  private void sortDatasetsIntoRenderOrder(int[] renderOrder) {
    final int numDatasets = renderOrder.length;
    int d = 0;

    Focus focus = plot.getFocus();

    //  all unfocused barcharts first
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer renderer = plot.getRenderer(i);
      if (renderer instanceof BarChartXYRenderer
          && (focus == null || focus.getDatasetIndex() != i)) {
        renderOrder[d++] = i;
      }
    }

    // next render unfocused non-barcharts
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer renderer = plot.getRenderer(i);
      if (!(renderer instanceof BarChartXYRenderer)
          && (focus == null || focus.getDatasetIndex() != i)) {
        renderOrder[d++] = i;
      }
    }

    // finally render the focused series
    if (focus != null) {
      int num = plot.getFocus().getDatasetIndex();
      renderOrder[d++] = num;
    }
  }
}
