package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for iterating over {@link Dataset}s in a defined drawing order, 
 * and then deciding on how to map the visible portions of the domain onto the 
 * associated dataset renderers.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class XYPlotRenderer<T extends Tuple2D> {
  protected RenderState renderState = new RenderState();
  
  // Stores the order in which the datasets should be rendered.  For example, if
  // element [0] = 4, then this means the 5th dataset should be rendered first.
  private int[] datasetRenderOrder;
  
  private List<DrawableDataset> drawableDatasets;
  
  private XYPlot<T> plot;
  
  private void computeVisibleDomainAndRange(List<DrawableDataset> dds, 
      Interval plotDomain) {
    
    final int numDatasets = dds.size();
    
    for (int datasetIdx = 0; datasetIdx < numDatasets; datasetIdx++) {
      DrawableDataset drawableDataset = dds.get(datasetIdx);
      Dataset dataSet = drawableDataset.dataset;

      final double plotDomainStart = plotDomain.getStart();
      final double plotDomainLength = plotDomain.length();
      if (!(MathUtil.isBounded(plotDomainStart, dataSet.getDomainBegin()
          - plotDomainLength, dataSet.getDomainEnd()))) {
        continue;
      }

      final int maxPoints = drawableDataset.maxDrawablePoints;
      int domainStartIdx = 0;
      int domainEndIdx = 0;
      int mipLevel = -1;
      do {
        mipLevel++;
        domainStartIdx = Util.binarySearch(dataSet, plotDomainStart, mipLevel);
        domainEndIdx = Util.binarySearch(dataSet, plotDomainStart + plotDomainLength, mipLevel);
      } while ((domainEndIdx - domainStartIdx) > maxPoints);

      if (drawableDataset.currMipLevel != mipLevel) {
        drawableDataset.currMipLevel = mipLevel;
        plot.getHoverPoints()[datasetIdx] = DefaultXYPlot.NO_SELECTION;
      }
      
      drawableDataset.visDomainStartIndex = domainStartIdx;
      drawableDataset.visDomainEndIndex = domainEndIdx;

      double rangeMin = Double.POSITIVE_INFINITY;
      double rangeMax = Double.NEGATIVE_INFINITY;
      for (int i = domainStartIdx; i <= domainEndIdx; i++) {
        double y = dataSet.getFlyweightTuple(i, mipLevel).getSecond();
        rangeMin = Math.min(rangeMin, y);
        rangeMax = Math.max(rangeMax, y);
      }
      drawableDataset.visRangeMin = rangeMin;
      drawableDataset.visRangeMax = rangeMax;
    }
  }

  private void drawDataset(int datasetIndex, Layer layer, XYPlot<T> plot) {
    DrawableDataset dds = drawableDatasets.get(datasetIndex);
    
    Dataset<T> dataSet = dds.dataset;
    DatasetRenderer<T> renderer = dds.renderer;
    
    if (dataSet.getNumSamples(0) < 2) {
      return;
    }

    Focus focus = plot.getFocus();
    int focusSeries, focusPoint;
    if (focus == null) {
      focusSeries = -1;
      focusPoint = -1;
    } else {
      focusSeries = focus.getDatasetIndex();
      focusPoint = focus.getPointIndex();
    }

    renderState
        .setDisabled((focusSeries != -1) && (focusSeries != datasetIndex));

    renderer.beginCurve(layer, renderState);

    int domainStart = dds.visDomainStartIndex;
    int domainEnd = dds.visDomainEndIndex;
    int mipLevel = dds.currMipLevel;
    int numSamples = dataSet.getNumSamples(mipLevel);

    // Render the data curve
    int end = Math.min(domainEnd + 1, numSamples);
    int methodCallCount = 0;
    for (int i = Math.max(0, domainStart - 1); i < end; i++) {
      Tuple2D dataPt = dataSet.getFlyweightTuple(i, mipLevel);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      // FIXME: refactor to remove cast
      renderer.drawCurvePart(layer, (T)dataPt, methodCallCount++, renderState);
    }
    renderer.endCurve(layer, renderState);
    
    // Render the focus points on the curve
    renderer.beginPoints(layer, renderState);
    end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 2); i < end; i++) {
      Tuple2D dataPt = dataSet.getFlyweightTuple(i, mipLevel);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      // FIXME: refactor to remove cast
      renderer.drawPoint(layer, (T)dataPt, renderState);
    }
    renderer.endPoints(layer, renderState);
  }

  public void drawDatasets() {
    computeVisibleDomainAndRange(drawableDatasets, plot.getDomain());
    setupRangeAxisVisibleRanges(drawableDatasets);

    final int numDatasets = plot.getDatasets().size();
    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < numDatasets; i++) {
      drawDataset(datasetRenderOrder[i], plotLayer, plot);
    }
  }

  public void drawHoverPoints() {
    Layer layer = plot.getHoverLayer();
    layer.save();
    layer.clear();
    
    int[] hoverPoints = plot.getHoverPoints();
    
    for (int i = 0; i < hoverPoints.length; i++) {
      DrawableDataset dds = drawableDatasets.get(i);
      final int hoverPoint = hoverPoints[i];
      if (hoverPoint != -1) {
        Dataset<T> dataset = dds.dataset;
        T dataPt = dataset.getFlyweightTuple(hoverPoint, dds.currMipLevel);
        dds.renderer.drawHoverPoint(layer, dataPt, i);
      }
    }
    
    layer.restore();
  }
  
  public DrawableDataset getDrawableDataset(int datasetIndex) {
    return drawableDatasets.get(datasetIndex);
  }
  
  public void init() {
    drawableDatasets = new ArrayList<DrawableDataset>();
    Datasets datasets = plot.getDatasets();
    for (int i = 0; i < datasets.size(); i++) {
      DatasetRenderer renderer = plot.getDatasetRenderer(i);
      int maxDrawablePoints = Math.min(plot.getMaxDrawableDataPoints(), 
          renderer.getMaxDrawableDatapoints());
      
      DrawableDataset drawableDataset = new DrawableDataset();
      drawableDataset.dataset = datasets.get(i);
      drawableDataset.renderer = renderer;
      drawableDataset.maxDrawablePoints = maxDrawablePoints;
      drawableDataset.currMipLevel = 0;
      
      drawableDatasets.add(drawableDataset);
    }

    sortDatasetsIntoRenderOrder();
  }
  
  public void setPlot(XYPlot<T> plot) {
    this.plot = plot;
  }
  
  private void setupRangeAxisVisibleRanges(List<DrawableDataset> dds) {
    for (int i = 0; i < dds.size(); i++) {
      DrawableDataset ds = dds.get(i);
      RangeAxis ra = plot.getRangeAxis(i);
      ra.setVisibleRange(ds.visRangeMin, ds.visRangeMax);
    }
  }

  private void sortDatasetsIntoRenderOrder() {
    final int numDatasets = drawableDatasets.size();
    if (datasetRenderOrder == null || numDatasets != datasetRenderOrder.length) {
      datasetRenderOrder = new int[numDatasets];
    }
    
    int d = 0;
    Focus focus = plot.getFocus();

    //  all unfocused barcharts first
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer<T> renderer = drawableDatasets.get(i).renderer;
      if (renderer instanceof BarChartXYRenderer
          && (focus == null || focus.getDatasetIndex() != i)) {
        datasetRenderOrder[d++] = i;
      }
    }

    // next render unfocused non-barcharts
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer<T> renderer = drawableDatasets.get(i).renderer;
      if (!(renderer instanceof BarChartXYRenderer)
          && (focus == null || focus.getDatasetIndex() != i)) {
        datasetRenderOrder[d++] = i;
      }
    }

    // finally render the focused series
    if (focus != null) {
      int num = plot.getFocus().getDatasetIndex();
      datasetRenderOrder[d++] = num;
    }
  }
}
