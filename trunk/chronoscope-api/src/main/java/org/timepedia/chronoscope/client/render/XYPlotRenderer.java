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

  protected XYPlot<T> plot;

  protected View view;

  private void calcVisibleDomainAndRange(List<DrawableDataset<T>> dds,
      Interval plotDomain) {

    final int numDatasets = dds.size();

    for (int i = 0; i < plot.getRangeAxisCount(); i++) {
      plot.getRangeAxis(i).resetVisibleRange();
    }

    for (int datasetIdx = 0; datasetIdx < numDatasets; datasetIdx++) {
      DrawableDataset<T> drawableDataset = dds.get(datasetIdx);
      Dataset<T> dataSet = drawableDataset.dataset;

      if (!plotDomain.intersects(dataSet.getDomainExtrema())) {
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
      domainStartIdx = Math.max(0, domainStartIdx - 1);
      domainEndIdx = Math.min(domainEndIdx, dataSet.getNumSamples() - 1);

      drawableDataset.visDomainStartIndex = domainStartIdx;
      drawableDataset.visDomainEndIndex = domainEndIdx;

      RangeAxis rangeAxis = plot.getRangeAxis(datasetIdx);
      Interval visRange = calcVisibleRange(bestMipMap, domainStartIdx,
          domainEndIdx, drawableDataset.getRenderer());

      if (rangeAxis.isCalcRangeAsPercent()) {
        final double refY = calcReferenceY(rangeAxis, drawableDataset);
        double maxY = visRange.getEnd();
        double minY = visRange.getStart();
        visRange.setEndpoints(RangeAxis.calcPrctDiff(refY, minY),
            RangeAxis.calcPrctDiff(refY, maxY));
      }

      rangeAxis.adjustVisibleRange(visRange);
    }
  }

  /**
   * Calculates the range-Y extrema values of the specified {@link MipMap}.
   */
  private Interval calcVisibleRange(MipMap mipMap, int domainStartIdx,
      int domainEndIdx, DatasetRenderer<T> renderer) {
    double rangeMin = Double.POSITIVE_INFINITY;
    double rangeMax = Double.NEGATIVE_INFINITY;
    Iterator<Tuple2D> tupleItr = mipMap.getTupleIterator(domainStartIdx);
    for (int i = domainStartIdx; i <= domainEndIdx; i++) {
      double y = renderer.getRange(tupleItr.next());
      rangeMin = Math.min(rangeMin, y);
      rangeMax = Math.max(rangeMax, y);
    }

    return new Interval(rangeMin, rangeMax);
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

    
    MipMap currMipMap = dds.currMipMap;
    final int domainStartIdx = dds.visDomainStartIndex;
    final int domainEndIdx = dds.visDomainEndIndex;

    RangeAxis rangeAxis = plot.getRangeAxis(datasetIndex);
    final boolean calcRangeAsPercent = rangeAxis.isCalcRangeAsPercent();

    // Render the curve

    final double refY = calcReferenceY(rangeAxis, dds);

    int methodCallCount = 0;
    for (int pass = 0; pass < renderer.getNumPasses(dds.dataset); pass++) {
      renderState
              .setDisabled((focusSeries != -1) && (focusSeries != datasetIndex));

      renderer.beginCurve(layer, renderState);
      
      Iterator<Tuple2D> tupleItr = currMipMap.getTupleIterator(domainStartIdx);
      renderState.setPassNumber(pass);
      for (int i = domainStartIdx; i <= domainEndIdx; i++) {
        Tuple2D dataPt = tupleItr.next();
        renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
        
        if (calcRangeAsPercent) {
          LocalTuple tmpTuple = new LocalTuple();
          tmpTuple.setXY(dataPt.getDomain(),
              RangeAxis.calcPrctDiff(refY, dataPt.getRange0()));
          dataPt = tmpTuple;
        }
        // FIXME: refactor to remove cast
        renderer
            .drawCurvePart(layer, (T) dataPt, methodCallCount++, renderState);
      }
      renderer.endCurve(layer, renderState);

      // Render the focus points on the curve
      renderer.beginPoints(layer, renderState);
      //startIdx = Math.max(0, domainStartIdx - 2);
      tupleItr = currMipMap.getTupleIterator(domainStartIdx);
      for (int i = domainStartIdx; i <= domainEndIdx; i++) {
        Tuple2D dataPt = tupleItr.next();
        renderState.setFocused(focusSeries == datasetIndex && focusPoint == i &&
        renderer.getFocusDimension(pass) == focus.getDatasetIndex());

        if (calcRangeAsPercent) {
          LocalTuple tmpTuple = new LocalTuple();
          tmpTuple.setXY(dataPt.getDomain(),
              RangeAxis.calcPrctDiff(refY, dataPt.getRange0()));
          dataPt = tmpTuple;
        }
        // FIXME: refactor to remove cast
        renderer.drawPoint(layer, (T) dataPt, renderState);
      }
      renderer.endPoints(layer, renderState);
    }
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
    calcVisibleDomainAndRange(drawableDatasets, plot.getDomain());

    final int numDatasets = plot.getDatasets().size();
    Layer plotLayer = plot.getPlotLayer();
    for (int i = 0; i < numDatasets; i++) {
      drawDataset(datasetRenderOrder[i], plotLayer, plot);
    }
  }

  public void drawHoverPoints(Layer layer) {
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
        RangeAxis rangeAxis = plot.getRangeAxis(i);

        if (rangeAxis.isCalcRangeAsPercent()) {
          // Need to store domain/range values from the flyweight dataPt object
          // in tmp variables, because currMipMap.getTuple() overwrites the 
          // dataPt object's state
          final double hoverX = dataPt.getDomain();
          final double hoverY = dataPt.getRange0();

          final double refY = calcReferenceY(rangeAxis, dds);

          LocalTuple tmpTuple = new LocalTuple();
          tmpTuple.setXY(hoverX, RangeAxis.calcPrctDiff(refY, hoverY));
          dataPt = (T) (Object) tmpTuple;
        }
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

    if (!"auto".equals(seriesProp.display)) {
      renderer = this.datasetRendererMap.newDatasetRenderer(seriesProp.display);
    } else {
      renderer = this.datasetRendererMap.get(dataset);
    }

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
      if (!dds.getRenderer().isCustomInstalled()) {

        GssElement gssElem = new GssElementImpl("series", null, "s" + index);
        GssProperties props = view.getGssProperties(gssElem, "");
        String renderType = dds.dataset.getPreferredRenderer();
        if (renderType == null || renderType.equals("")) {
          renderType = "line";
        }
        if (!"auto".equals(props.display)) {
          renderType = props.display;
        }
        DatasetRenderer dr = datasetRendererMap.newDatasetRenderer(renderType);
        configRenderer(dr, index, gssElem);
        dds.setRenderer(dr);
        dds.maxDrawablePoints = dr.getMaxDrawableDatapoints();
      }
      index++;
    }
    sortDatasetsIntoRenderOrder();
  }

  public double calcReferenceY(RangeAxis ra, DrawableDataset dds) {
    final int refYIndex = ra.isAutoZoomVisibleRange() ? dds.visDomainStartIndex
        : 0;
    return dds.getRenderer().getRange(dds.currMipMap.getTuple(refYIndex));
  }

  private static final class LocalTuple implements Tuple2D {

    private double x, y;

    public void setXY(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public double getDomain() {
      return x;
    }

    public double getRange(int index) {
      if (index == 0) {
        return y;
      }
      throw new UnsupportedOperationException(
          "unsupported tuple index: " + index);
    }

    public double getRange0() {
      return y;
    }

    public int size() {
      return 2;
    }

    public String toString() {
      return "x=" + (long) x + "; y=" + y;
    }
  }
}
