package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.MipMapRegion;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Responsible for iterating over {@link Dataset}s in a defined drawing order,
 * and then deciding on how to map the visible portions of the domain onto the
 * associated {@link DatasetRenderer}s.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class XYPlotRenderer<T extends Tuple2D> {

  protected RenderState renderState = new RenderState();

  private DatasetRendererMap datasetRendererMap = new DatasetRendererMap();

  // Stores the order in which the datasets should be rendered.  For example, if
  // element [0] = 4, then this means the 5th dataset should be rendered first.
  private int[] datasetRenderOrder;

  private List<DrawableDataset<T>> drawableDatasets;

  private boolean initialized = false;

  private XYPlot<T> plot;

  private View view;

  private void computeVisibleDomainAndRange(List<DrawableDataset<T>> dds,
      Interval plotDomain) {

    final int numDatasets = dds.size();

    for (int datasetIdx = 0; datasetIdx < numDatasets; datasetIdx++) {
      DrawableDataset<T> drawableDataset = dds.get(datasetIdx);
      Dataset<T> dataSet = drawableDataset.dataset;

      final double plotDomainStart = plotDomain.getStart();
      final double plotDomainEnd = plotDomain.getEnd();
      Interval domainExtrema = dataSet.getDomainExtrema();
      if (!(MathUtil.isBounded(plotDomainStart,
          domainExtrema.getStart() - plotDomain.length(),
          domainExtrema.getEnd()))) {
        continue;
      }

      // Find the highest-resolution mipmap whose number of data points
      // that lie within the plot domain is <= maxDataPoints.
      final int maxDrawableDataPoints = getMaxDrawableDataPoints(
          drawableDataset);
      MipMapRegion bestMipMapRegion = dataSet
          .getBestMipMapForInterval(plotDomain, maxDrawableDataPoints);

      MipMap bestMipMap = bestMipMapRegion.getMipMap();
      if (drawableDataset.currMipMap.getLevel() != bestMipMap.getLevel()) {
        drawableDataset.currMipMap = bestMipMap;
        plot.getHoverPoints()[datasetIdx] = DefaultXYPlot.NO_SELECTION;
      }

      int domainStartIdx = bestMipMapRegion.getStartIndex();
      int domainEndIdx = bestMipMapRegion.getEndIndex();
      
      drawableDataset.visDomainStartIndex = domainStartIdx;
      drawableDataset.visDomainEndIndex = domainEndIdx;

      calcVisualRange(drawableDataset, bestMipMap, domainStartIdx,
          domainEndIdx);
    }
  }

  /**
   * Calculates the range-Y extrema values of the specified MipMap and assigns
   * these values to drawableDataset.
   */
  private void calcVisualRange(DrawableDataset<T> drawableDataset,
      MipMap mipMap, int domainStartIdx, int domainEndIdx) {

    double rangeMin = Double.POSITIVE_INFINITY;
    double rangeMax = Double.NEGATIVE_INFINITY;
    Iterator<Tuple2D> tupleItr = mipMap.getTupleIterator(domainStartIdx);
    for (int i = domainStartIdx; i <= domainEndIdx; i++) {
      double y = tupleItr.next().getRange0();
      rangeMin = Math.min(rangeMin, y);
      rangeMax = Math.max(rangeMax, y);
    }
    drawableDataset.visRangeMin = rangeMin;
    drawableDataset.visRangeMax = rangeMax;
  }

  private void drawDataset(int datasetIndex, Layer layer, XYPlot<T> plot) {
    DrawableDataset dds = drawableDatasets.get(datasetIndex);

    Dataset<T> dataSet = dds.dataset;
    DatasetRenderer<T> renderer = dds.getRenderer();

    if (dataSet.getNumSamples() < 2) {
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

    MipMap currMipMap = dds.currMipMap;
    final int domainStart = dds.visDomainStartIndex;
    final int domainEnd = dds.visDomainEndIndex;
    final int numSamples = currMipMap.size();

    // Render the data curve
    int startIdx = Math.max(0, domainStart - 1);
    int endIdx = Math.min(domainEnd, numSamples - 1);

    //System.out.println("TESTING: XYPlotRenderer: " + (endIdx - startIdx + 1));
    //System.out.println("TESTING: XYPlotRenderer: startIdx=" + startIdx + "; endIdx=" + endIdx);

    int methodCallCount = 0;
    Iterator<Tuple2D> tupleItr = currMipMap.getTupleIterator(startIdx);
    for (int i = startIdx; i <= endIdx; i++) {
      Tuple2D dataPt = tupleItr.next();
      //System.out.println("TESTING: XYPlotRenderer draw curve: startIdx=" + startIdx + "; startDate = " + startDate);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      // FIXME: refactor to remove cast
      renderer.drawCurvePart(layer, (T) dataPt, methodCallCount++, renderState);
    }
    renderer.endCurve(layer, renderState);

    // Render the focus points on the curve
    renderer.beginPoints(layer, renderState);
    startIdx = Math.max(0, domainStart - 2);
    endIdx = Math.min(domainEnd, numSamples - 1);
    //System.out.println("TESTING: XYPlotRenderer draw points: startIdx=" + startIdx + "; startDate = " + startDate);
    tupleItr = currMipMap.getTupleIterator(startIdx);
    for (int i = startIdx; i <= endIdx; i++) {
      Tuple2D dataPt = tupleItr.next();
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      // FIXME: refactor to remove cast
      renderer.drawPoint(layer, (T) dataPt, renderState);
    }
    renderer.endPoints(layer, renderState);
  }

  public Interval calcWidestPlotDomain() {
    if (drawableDatasets.isEmpty()) {
      return null;
    }

    Interval widestPlotDomain = null;
    for (DrawableDataset<T> dds : drawableDatasets) {
      Dataset<T> ds = dds.dataset;
      final int maxDrawableDataPoints = getMaxDrawableDataPoints(dds);
      MipMap mm = ds.getMipMapChain()
          .findHighestResolution(maxDrawableDataPoints);

      Interval drawableDomain = dds.getRenderer()
          .getDrawableDomain(mm.getDomain());
      if (widestPlotDomain == null) {
        widestPlotDomain = drawableDomain;
      } else {
        widestPlotDomain.expand(drawableDomain);
      }
    }

    return widestPlotDomain;
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
        // TODO: add generics to MipMap class to remove this cast
        T dataPt = (T) dds.currMipMap.getTuple(hoverPoint);
        //T dataPt = dataset.getFlyweightTuple(hoverPoint, dds.currMipMap.getLevel());
        dds.getRenderer().drawHoverPoint(layer, dataPt, i);
      }
    }

    layer.restore();
  }

  public DrawableDataset<T> getDrawableDataset(int datasetIndex) {
    DrawableDataset<T> dds = drawableDatasets.get(datasetIndex);
    return dds;
  }

  public void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");

    drawableDatasets = new ArrayList<DrawableDataset<T>>();
    Datasets<T> datasets = plot.getDatasets();
    for (int i = 0; i < datasets.size(); i++) {
      addDataset(i, datasets.get(i));
    }

    initialized = true;
  }

  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Adds a new dataset to the list of datasets that this renderer is
   * responsible for.
   */
  public void addDataset(int datasetIndex, Dataset<T> dataset) {
    ArgChecker.isNotNull(dataset, "dataset");

    DrawableDataset<T> drawableDataset = new DrawableDataset<T>();
    drawableDataset.dataset = dataset;
    GssElement gssElem = new GssElementImpl("series", null, "s" + datasetIndex);
    GssProperties seriesProp = view.getGssProperties(gssElem, "");
    DatasetRenderer<T> renderer = null;

    if(!"auto".equals(seriesProp.display)) {
      renderer = this.datasetRendererMap.newDatasetRenderer(seriesProp.display);
    }
    else
      renderer = this.datasetRendererMap.get(dataset);

    configRenderer(renderer, datasetIndex, gssElem);
    drawableDataset.setRenderer(renderer);
    drawableDataset.currMipMap = drawableDataset.dataset.getMipMapChain()
        .getMipMap(0);
    drawableDataset.maxDrawablePoints = renderer.getMaxDrawableDatapoints();

    drawableDatasets.add(drawableDataset);
    sortDatasetsIntoRenderOrder();
  }

  /**
   * Sets the map that determines which {@link DatasetRenderer} to use for a
   * given {@link Dataset} object.
   */
  public void setDatasetRendererMap(DatasetRendererMap datasetRendererMap) {
    this.datasetRendererMap = datasetRendererMap;
  }

  /**
   * Removes the specified dataset from the list of datasets that this renderer
   * is responsible for.
   */
  public void removeDataset(Dataset<T> dataset) {
    ArgChecker.isNotNull(dataset, "dataset");

    boolean wasDatasetFound = false;
    for (int i = 0; i < drawableDatasets.size(); i++) {
      DrawableDataset<T> drawableDataset = this.drawableDatasets.get(i);
      if (dataset == drawableDataset.dataset) {
        drawableDatasets.remove(i);
        drawableDataset.invalidate();
        wasDatasetFound = true;
        break;
      }
    }

    // throw a fit if we can't find the dataset-to-be-removed
    if (!wasDatasetFound) {
      throw new RuntimeException(
          "dataset did not exist in drawableDatasets list");
    }

    // Need to re-assign the datasetIndex value across all renderers to that 
    // the indices are consecutive (necessary in the case where a dataset
    // in the middle of the list is removed).
    for (int i = 0; i < drawableDatasets.size(); i++) {
      drawableDatasets.get(i).getRenderer().setDatasetIndex(i);
    }
  }

  public void setPlot(XYPlot<T> plot) {
    this.plot = plot;
  }

  public void resetMipMapLevels() {
    for (DrawableDataset<T> dds : this.drawableDatasets) {
      dds.currMipMap = dds.dataset.getMipMapChain().getMipMap(0);
    }
  }

  /**
   * Associates a {@link Dataset} (via its ordinal position in the {@link
   * Datasets} collection) with a {@link DatasetRenderer}.
   */
  public void setDatasetRenderer(int datasetIndex,
      DatasetRenderer<T> renderer) {
    ArgChecker.isNotNull(renderer, "renderer");
    GssElement gssElem = new GssElementImpl("series", null, "s" + datasetIndex);
    configRenderer(renderer, datasetIndex, gssElem);
    DrawableDataset<T> dds = this.getDrawableDataset(datasetIndex);
    dds.currMipMap = dds.dataset.getMipMapChain().getMipMap(0);
    dds.maxDrawablePoints = renderer.getMaxDrawableDatapoints();
    dds.setRenderer(renderer);
  }

  public void setView(View view) {
    this.view = view;
  }

  private void setupRangeAxisVisibleRanges(List<DrawableDataset<T>> dds) {
    for (int i = 0; i < dds.size(); i++) {
      DrawableDataset<T> ds = dds.get(i);
      RangeAxis ra = plot.getRangeAxis(i);
      ra.setVisibleRange(ds.visRangeMin, ds.visRangeMax);
    }
  }

  private void sortDatasetsIntoRenderOrder() {
    final int numDatasets = drawableDatasets.size();
    if (datasetRenderOrder == null
        || numDatasets != datasetRenderOrder.length) {
      datasetRenderOrder = new int[numDatasets];
    }

    int d = 0;
    Focus focus = plot.getFocus();

    //  all unfocused barcharts first
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer<T> renderer = drawableDatasets.get(i).getRenderer();
      if (renderer instanceof BarChartXYRenderer && (focus == null
          || focus.getDatasetIndex() != i)) {
        datasetRenderOrder[d++] = i;
      }
    }

    // next render unfocused non-barcharts
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer<T> renderer = drawableDatasets.get(i).getRenderer();
      if (!(renderer instanceof BarChartXYRenderer) && (focus == null
          || focus.getDatasetIndex() != i)) {
        datasetRenderOrder[d++] = i;
      }
    }

    // finally render the focused series
    if (focus != null) {
      int num = plot.getFocus().getDatasetIndex();
      datasetRenderOrder[d++] = num;
    }
  }

  private void configRenderer(DatasetRenderer<T> renderer, int datasetIndex,
      GssElement gssElem) {
    ArgChecker.isNotNull(renderer, "renderer");

    renderer.setParentGssElement(gssElem);
    renderer.setPlot(this.plot);
    renderer.setDatasetIndex(datasetIndex);
    renderer.initGss(this.view);
  }

  /**
   * Returns the lesser of what the {@link XYPlot} and the {@link
   * DatasetRenderer} report as the maximum number of datapoints that they can
   * handle.
   */
  private int getMaxDrawableDataPoints(DrawableDataset dds) {
    return Math.min(dds.maxDrawablePoints, plot.getMaxDrawableDataPoints());
  }

  public void checkForGssChanges() {
    int index = 0;
    for (DrawableDataset<T> dds : this.drawableDatasets) {

      dds.currMipMap = dds.dataset.getMipMapChain().getMipMap(0);
      GssElement gssElem = new GssElementImpl("series", null, "s"+index);
      GssProperties props = view.getGssProperties(gssElem, "");
      String renderType = dds.dataset.getPreferredRenderer();
      if(renderType == null || renderType.equals("")) renderType = "line";
      if(!"auto".equals(props.display)) {
        renderType = props.display;
      }
      DatasetRenderer dr = datasetRendererMap.newDatasetRenderer(renderType);
      configRenderer(dr, index, gssElem);
      dds.setRenderer(dr);
      dds.maxDrawablePoints = dr.getMaxDrawableDatapoints();

      index++;

    }
    sortDatasetsIntoRenderOrder();
  }
}
