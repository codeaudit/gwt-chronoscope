package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYDatasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.AxisPanel;
import org.timepedia.chronoscope.client.axis.DateAxis;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.axis.AxisPanel.Position;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.XYDatasetListener;
import org.timepedia.chronoscope.client.render.Background;
import org.timepedia.chronoscope.client.render.GssBackground;
import org.timepedia.chronoscope.client.render.ScalableXYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYLineRenderer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYRenderer;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A DefaultXYPlot is responsible for drawing the main chart area (excluding
 * axes), mapping one or more datasets from (domain,range) space to (x,y) screen
 * space by delegating to one or more ValueAxis implementations. Drawing for
 * each dataset is delegated to Renderers. A plot also maintains state like the
 * current selection and focus point.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public class DefaultXYPlot implements XYPlot, Exportable, XYDatasetListener {

  private static int globalPlotNumber = 0;

  // The maximum distance that the mouse pointer can stray from a candidate
  // data point and still be considered as referring to that point.
  private static final int MAX_FOCUS_DIST = 8;

  // The maximum distance (only considers x-axis) that the mouse pointer can 
  // stray from a data point and still cause that point to be "hovered".
  private static final int MAX_HOVER_DIST = 8;

  private static int MAX_DRAWABLE_DATAPOINTS = 400;

  private static final double MIN_PLOT_HEIGHT = 50;

  // Indicator that nothing is selected (e.g. a data point or a data set).
  private static final int NO_SELECTION = -1;

  private static final double ZOOM_FACTOR = 1.50d;

  private PortableTimerTask animationContinuation;

  private PortableTimer animationTimer;

  private Background background;
  
  private double beginHighlight = Double.MIN_VALUE, endHighlight = Double.MIN_VALUE;

  private int currentMiplevels[];

  private Chart chart;

  private XYDatasets datasets;
  
  private DateAxis domainAxis;

  private Bounds domainBounds;

  private Layer domainLayer;

  private AxisPanel domainPanel;

  private boolean drewVertical;

  private Focus focus = null;

  private boolean highlightDrawn;

  private Layer highLightLayer;

  private int[] hoverPoints;

  private final Map<String, RangeAxis> id2rangeAxis
      = new HashMap<String, RangeAxis>();

  private Bounds innerBounds;

  private final boolean interactive;
  
  private boolean isAnimating = false;

  private LegendAxis legendAxis;

  private boolean legendEnabled = true;

  private final NearestPoint nearestSingleton = new NearestPoint();

  private ArrayList<Overlay> overlays;

  private OverviewAxis overviewAxis;

  private boolean overviewDrawn = false;

  private boolean overviewEnabled = true;

  private Layer overviewLayer;

  private Bounds plotBounds;

  private Interval plotDomain, lastPlotDomain;
  
  private Layer plotLayer;

  private int plotNumber = 0;

  private XYPlotRenderer plotRenderer;

  // Maps a dataset id to the RangeAxis to which it has been bound.
  // E.g. axes[2] returns the RangeAxis that datasets.get(2) is 
  // bound to.  The relationship from dataset to axis is 
  // many-to-one.
  private RangeAxis[] rangeAxes;

  private AxisPanel rangePanelLeft, rangePanelRight;

  private Bounds topBounds;

  private Layer topLayer;

  private AxisPanel topPanel;

  private Layer verticalAxisLayer;

  private View view;

  private double visibleDomainMax;

  private final XYRenderer[] xyRenderers;

  private enum DistanceFormula {

    /**
     * The distance from point (x1,x2) to point (y1,y2) on an XY plane.
     */
    XY {double dist(double x1, double y1, double x2, double y2) {
      return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }},
    /**
     * Considers only the distance between x1 and x2, ignoring the y values of
     * points (x1,y1) and (x2,y2).
     */
    X_ONLY {double dist(double x1, double y1, double x2, double y2) {
      return Math.abs(x1 - x2);
    }};

    /**
     * The distance from points (x1,y1) to (x2,y2).
     */
    abstract double dist(double x1, double y1, double x2, double y2);
  }
  
  public DefaultXYPlot(XYDataset[] ds, boolean interactive) {
    ArgChecker.isNotNull(ds, "ds");
    ArgChecker.isGT(ds.length, 0, "ds.length");
    
    this.datasets = new XYDatasets(ds);
    this.interactive = interactive;

    MAX_DRAWABLE_DATAPOINTS = 100 / ds.length;
    overlays = new ArrayList<Overlay>();
    xyRenderers = new XYRenderer[ds.length];

    hoverPoints = new int[ds.length];
    resetHoverPoints();

    plotRenderer = new ScalableXYPlotRenderer(this);
    plotNumber = globalPlotNumber++;
    datasets.addListener(this);
  }

  /**
   * @gwt.export
   */
  @Export
  public void addOverlay(Overlay overlay) {
    overlays.add(overlay);
    overlay.setPlot(this);
  }

  public void animateTo(final double destDomainOrigin,
      final double destCurrentDomain, final int eventType) {
    
    animateTo(destDomainOrigin, destCurrentDomain, eventType, null);
  }

  public void animateTo(final double destDomainOrigin,
      final double destCurrentDomain, final int eventType,
      final PortableTimerTask continuation) {

    animateTo(destDomainOrigin, destCurrentDomain, eventType, continuation,
        true);
  }

  private void animateTo(final double destDomainOrigin, final double destDomainLength, 
      final int eventType, final PortableTimerTask continuation, 
      boolean fence) {
    
    if (!isAnimatable()) {
      return;
    }
    
    // if there is already an animation running, cancel it
    if (animationTimer != null) {
      animationTimer.cancelTimer();
      if (animationContinuation != null) {
        animationContinuation.run(animationTimer);
      }
      animationTimer = null;
    }
    
    final Interval destDomain;
    if (fence) {
      destDomain = fenceDomain(destDomainOrigin, destDomainLength);
    }
    else {
      destDomain = new Interval(destDomainOrigin, destDomainOrigin + destDomainLength);
    }
    
    animationContinuation = continuation;
    final Interval visibleDomain = this.plotDomain;
    
    animationTimer = view.createTimer(new PortableTimerTask() {
      final double destDomainMid = destDomain.midpoint();
      final Interval srcDomain = visibleDomain.copy();
      // Ratio of destination domain to current domain
      final double zoomFactor = destDomain.length() / srcDomain.length();

      double startTime = 0;
      boolean lastFrame = false;

      public void run(PortableTimer t) {
        isAnimating = true;
        if (startTime == 0) {
          startTime = t.getTime();
        }
        double curTime = t.getTime();
        double lerpFactor = (curTime - startTime) / 300;
        if (lerpFactor > 1) {
          lerpFactor = 1;
        }
        
        final double domainCenter = (destDomainMid - srcDomain.midpoint()) * lerpFactor + srcDomain.midpoint();
        final double domainLength = srcDomain.length() * ((1 - lerpFactor) + (zoomFactor * lerpFactor));
        final double domainStart = domainCenter - domainLength / 2;
        visibleDomain.setEndpoints(domainStart, domainStart + domainLength);
        redraw();

        if (lerpFactor < 1) {
          t.schedule(10);
        } 
        else if (lastFrame) {
          final double domainAmt = srcDomain.getStart() - visibleDomain.getStart();
          view.fireScrollEvent(DefaultXYPlot.this, domainAmt, eventType, false);
          if (continuation != null) {
            continuation.run(t);
            animationContinuation = null;
          }
          isAnimating = false;
          animationTimer = null;
          redraw();
        }
        else {
          lastFrame = true;
          animationTimer.schedule(300);
        } 
      }
    });

    animationTimer.schedule(10);
  }

  public boolean click(int x, int y) {
    if (setFocusXY(x, y)) {
      return true;
    }

    for (Overlay o : overlays) {
      double oPos = o.getDomainX();
      if (plotDomain.contains(oPos)) {
        if (o.isHit(x, y)) {
          o.click(x, y);
          return true;
        }
      }
    }

    return legendEnabled ? legendAxis.click(x, y) : false;
  }

  /**
   * Any cached drawings of this axis are flushed and redrawn on next update
   */
  public void damageAxes(ValueAxis axis) {
    drewVertical = false;
  }

  public double domainToScreenX(double dataX, int datasetIndex) {
    double userX = domainAxis.dataToUser(dataX);
    return userX * plotBounds.width;
  }
  
  public double domainToWindowX(double dataX, int datasetIndex) {
    return plotBounds.x + domainToScreenX(dataX, datasetIndex);
  }

  /**
   * @gwt.export
   */
  @Export
  public boolean ensureVisible(final double domainX, final double rangeY,
      PortableTimerTask callback) {
    view.ensureViewVisible();
    if (!plotDomain.containsOpen(domainX)) {
      scrollAndCenter(domainX, callback);
      return true;
    }
    return false;
  }

  public Chart getChart() {
    return chart;
  }

  public int getCurrentMipLevel(int datasetIndex) {
    return currentMiplevels[datasetIndex];
  }

  public double getDataX(int datasetIndex, int pointIndex) {
    return datasets.get(datasetIndex)
        .getX(pointIndex, currentMiplevels[datasetIndex]);
  }

  public double getDataY(int datasetIndex, int pointIndex) {
    return datasets.get(datasetIndex)
        .getY(pointIndex, currentMiplevels[datasetIndex]);
  }

  public ValueAxis getDomainAxis() {
    return domainAxis;
  }

  public double getVisibleDomainMax() {
    return visibleDomainMax;
  }
  
  public Interval getDomain() {
    return this.plotDomain;
  }

  public Focus getFocus() {
    return this.focus;
  }

  public int[] getHoverPoints() {
    return this.hoverPoints;
  }

  public String getHistoryToken() {
    return getChart().getChartId() + "(O" + plotDomain.getStart() + ",D"
        + plotDomain.length() + ")";
  }

  public Bounds getInnerBounds() {
    return innerBounds;
  }

  public int getMaxDrawableDataPoints() {
    return (int) (isAnimating ? MAX_DRAWABLE_DATAPOINTS : 1000);
  }

  public int getNearestVisiblePoint(double domainX, int datasetIndex) {
    return Util
        .binarySearch(datasets.get(datasetIndex), domainX, 
            currentMiplevels[datasetIndex]);
  }

  public OverviewAxis getOverviewAxis() {
    return overviewAxis;
  }

  public Layer getOverviewLayer() {
    return overviewLayer;
  }

  public Bounds getBounds() {
    return plotBounds;
  }

  /**
   * Returns the datasets associated with this plot.
   */
  public XYDatasets getDatasets() {
    return this.datasets;
  }
   
  public Layer getPlotLayer() {
    return initLayer(null, "plotLayer", plotBounds);
  }

  /**
   * @gwt.export getAxis
   */
  @Export("getAxis")
  public RangeAxis getRangeAxis(int datasetIndex) {
    return rangeAxes[datasetIndex];
  }

  public XYRenderer getRenderer(int datasetIndex) {
    return xyRenderers[datasetIndex];
  }

  public double getSelectionBegin() {
    return beginHighlight;
  }

  public double getSelectionEnd() {
    return endHighlight;
  }

  public boolean hasAxis(ValueAxis theAxis) {
    return topPanel.contains(theAxis) || domainPanel.contains(theAxis)
        || rangePanelLeft.contains(theAxis) || rangePanelRight
        .contains(theAxis);
  }

  public void init(View view) {
    ArgChecker.isNotNull(chart, "chart");
    ArgChecker.isNotNull(view, "view");
    this.view = view;
    this.focus = null;

    initViewIndependent();

    view.getCanvas().getRootLayer().setVisibility(true);

    domainPanel = new AxisPanel("domainAxisLayer" + plotNumber,
        Position.BOTTOM);
    domainAxis = new DateAxis(this, view, domainPanel);
    domainPanel.add(domainAxis);
    if (overviewEnabled) {
      overviewAxis = new OverviewAxis(this, view, domainPanel, "Overview");
      domainPanel.add(overviewAxis);
    }

    if (rangePanelLeft != null) {
      rangePanelLeft.layout();
    } else {
      rangePanelLeft = new AxisPanel("rangeAxisLayerLeft" + plotNumber,
          AxisPanel.Position.LEFT);
    }
    
    if (rangePanelRight != null) {
      rangePanelRight.layout();
    } else {
      rangePanelRight = new AxisPanel("rangeAxisLayerRight" + plotNumber,
          AxisPanel.Position.RIGHT);
    }

    this.rangeAxes = autoAssignDatasetAxes();

    topPanel = new AxisPanel("topPanel" + plotNumber, AxisPanel.Position.TOP);
    legendAxis = new LegendAxis(this, topPanel, "My graph");
    if (legendEnabled) {
      topPanel.add(legendAxis);
    }

    plotBounds = computePlotBounds();
    innerBounds = new Bounds(0, 0, plotBounds.width, plotBounds.height);

    clearDrawCaches();
    lastPlotDomain = plotDomain.copy();
    initLayers();
    background = new GssBackground(this);

    view.canvasSetupDone();
  }

  public boolean isAnimating() {
    return isAnimating;
  }

  public boolean isOverviewEnabled() {
    return this.overviewEnabled;
  }
  
  public void maxZoomOut() {
    pushHistory();
    double minDomain = datasets.getMinDomain();
    double maxDomain = datasets.getMaxDomain();
    animateTo(minDomain, maxDomain - minDomain, XYPlotListener.ZOOMED);
  }

  public boolean maxZoomTo(int x, int y) {
    int nearPointIndex = NO_SELECTION;
    int nearDataSetIndex = 0;
    double minNearestDist = MAX_FOCUS_DIST;

    for (int i = 0; i < datasets.size(); i++) {
      double domainX = windowXtoDomain(x);
      double rangeY = windowYtoRange(y, i);
      NearestPoint nearest = this.nearestSingleton;
      findNearestPt(domainX, rangeY, i, DistanceFormula.XY, nearest);
      if (nearest.dist < minNearestDist) {
        nearPointIndex = nearest.pointIndex;
        nearDataSetIndex = i;
        minNearestDist = nearest.dist;
      }
    }

    if (pointExists(nearPointIndex)) {
      maxZoomToPoint(nearPointIndex, nearDataSetIndex);
      return true;
    } else {
      return false;
    }
  }

  public void maxZoomToFocus() {
    if (focus != null) {
      maxZoomToPoint(focus.getPointIndex(), focus.getDatasetIndex());
    }
  }

  public void moveTo(double domainX) {
    final double domainAmtMoved = domainX - this.plotDomain.getStart();
    movePlotDomain(domainX);
    this.view.fireScrollEvent(this, domainAmtMoved, XYPlotListener.DRAGGED, false);
    this.redraw();
  }

  public void nextFocus() {
    shiftFocus(+1);
  }

  public void nextZoom() {
    pushHistory();
    double nDomain = plotDomain.length() / ZOOM_FACTOR;
    animateTo(plotDomain.midpoint() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
  }

  public void onDatasetAdded(XYDataset dataset) {
    this.initAndRedraw();
  }

  public void onDatasetChanged(XYDataset dataset, double domainStart,
      double domainEnd) {
    visibleDomainMax = Util.calcVisibleDomainMax(getMaxDrawableDataPoints(), datasets);
    int datasetIndex = this.datasets.indexOf(dataset);
    if (datasetIndex == - 1) {
      datasetIndex = 0;
    }
    damageAxes(getRangeAxis(datasetIndex));
    if (domainEnd > plotDomain.getEnd()) {
      animateTo(domainEnd - plotDomain.length() / 2, plotDomain.length(), 0,
          new PortableTimerTask() {
            public void run(PortableTimer timer) {
              initAndRedraw();
            }
          }, false);
    } else {
      initAndRedraw();
    }
  }

  public void onDatasetRemoved(XYDataset dataset) {
    if (datasets.isEmpty()) {
      throw new IllegalStateException(
          "XYDatasets container is empty -- can't render plot.");
    }
    
    initAndRedraw();
  }

  public InfoWindow openInfoWindow(final String html, final double domainX,
      final double rangeY, final int datasetIndex) {

    final InfoWindow window = view.createInfoWindow(html,
        domainToWindowX(domainX, datasetIndex),
        rangeToWindowY(rangeY, datasetIndex) + 5);

    if (ensureVisible(domainX, rangeY, new PortableTimerTask() {
      public void run(PortableTimer timer) {
        window.open();
      }
    })) {

    } else {
      window.open();
    }
    return window;
  }

  public void pageLeft(double pageSize) {
    page(-pageSize);
  }

  public void pageRight(double pageSize) {
    page(pageSize);
  }

  public void prevFocus() {
    shiftFocus(-1);
  }

  public void prevZoom() {
    pushHistory();
    double nDomain = plotDomain.length() * ZOOM_FACTOR;
    animateTo(plotDomain.midpoint() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
  }

  public double rangeToScreenY(double dataY, int datasetIndex) {
    double userY = getRangeAxis(datasetIndex).dataToUser(dataY);
    return plotBounds.height - userY * plotBounds.height;
  }

  public double rangeToWindowY(double rangeY, int datasetIndex) {
    return plotBounds.y + rangeToScreenY(rangeY, datasetIndex);
  }

  /**
   * @gwt.export
   */
  @Export
  public void redraw() {
    update();
    plotDomain.copyTo(lastPlotDomain);
    view.flipCanvas();
  }

  /**
   * @gwt.export
   */
  @Export
  public void reloadStyles() {
    overviewDrawn = false;
    Interval tmpPlotDomain = plotDomain.copy();
    init(view);
    ArrayList<Overlay> oldOverlays = overlays;
    overlays = new ArrayList<Overlay>();
    initializeDomain();
    redraw();
    tmpPlotDomain.copyTo(plotDomain);
    overlays = oldOverlays;
    redraw();
  }

  public void removeOverlay(Overlay over) {
    overlays.remove(over);
  }

  public void scrollAndCenter(double domainX, PortableTimerTask continuation) {
    pushHistory();

    final double newOrigin = domainX - plotDomain.length() / 2;
    animateTo(newOrigin, plotDomain.length(), XYPlotListener.CENTERED, continuation);
  }

  public void scrollPixels(int amt) {
    final double domainAmt = (double) amt / plotBounds.width * plotDomain.length();
    final double minDomain = datasets.getMinDomain();
    final double maxDomain = datasets.getMaxDomain();
    
    double newDomainOrigin = plotDomain.getStart() + domainAmt;
    if (newDomainOrigin + plotDomain.length() > maxDomain) {
      newDomainOrigin = maxDomain - plotDomain.length();
    } else if (newDomainOrigin < minDomain) {
      newDomainOrigin = minDomain;
    }
    movePlotDomain(newDomainOrigin);
    
    view.fireScrollEvent(DefaultXYPlot.this, domainAmt, 
        XYPlotListener.DRAGGED, false);
    redraw();
  }

  public void setAnimating(boolean animating) {
    this.isAnimating = animating;
  }

  public void setAutoZoomVisibleRange(int dataset, boolean autoZoom) {
    rangeAxes[dataset].setAutoZoomVisibleRange(autoZoom);
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  public void setCurrentMipLevel(int datasetIndex, int mipLevel) {
    if (currentMiplevels[datasetIndex] != mipLevel) {
      resetHoverPoints();
      // TODO: maybe adjust to nearest one in next level of detail
      currentMiplevels[datasetIndex] = mipLevel;
    }
  }

  public void setFocus(Focus focus) {
    this.focus = focus;
  }

  public boolean setFocusXY(int x, int y) {
    int nearestPt = NO_SELECTION;
    int nearestSer = 0;
    double minNearestDist = MAX_FOCUS_DIST;

    for (int i = 0; i < datasets.size(); i++) {
      double domainX = windowXtoDomain(x);
      double rangeY = windowYtoRange(y, i);
      NearestPoint nearest = this.nearestSingleton;
      findNearestPt(domainX, rangeY, i, DistanceFormula.XY, nearest);

      if (nearest.dist < minNearestDist) {
        nearestPt = nearest.pointIndex;
        nearestSer = i;
        minNearestDist = nearest.dist;
      }
    }

    final boolean somePointHasFocus = pointExists(nearestPt);
    if (somePointHasFocus) {
      setFocusAndNotifyView(nearestSer, nearestPt);
    } else {
      setFocusAndNotifyView(null);
    }

    redraw();
    return somePointHasFocus;
  }

  public void setHighlight(double begin, double end) {
    beginHighlight = begin;
    endHighlight = end;
  }

  public void setHighlight(int selStart, int selEnd) {

    int tmp = Math.min(selStart, selEnd);
    selEnd = Math.max(selStart, selEnd);
    selStart = tmp;
    beginHighlight = windowXtoDomain(selStart);
    endHighlight = windowXtoDomain(selEnd);
    redraw();
    // drawHighlight(highLightLayer, highLightLayer);
  }

  public boolean setHover(int x, int y) {

    // At the end of this method, this flag should be true iff *any* of the
    // datasets are sufficiently close to 1 or more of the dataset curves to
    // be considered "clickable".
    // closenessThreshold is the cutoff for "sufficiently close".
    boolean isCloseToCurve = false;
    final int closenessThreshold = MAX_FOCUS_DIST;

    // True iff one or more hoverPoints have changed since the last call to this method
    boolean isDirty = false;

    NearestPoint nearestHoverPt = this.nearestSingleton;
    for (int i = 0; i < datasets.size(); i++) {
      double dataX = windowXtoDomain(x);
      double dataY = windowYtoRange(y, i);
      findNearestPt(dataX, dataY, i, DistanceFormula.X_ONLY, nearestHoverPt);

      int nearestPointIdx = (nearestHoverPt.dist < MAX_HOVER_DIST)
          ? nearestHoverPt.pointIndex : NO_SELECTION;

      if (nearestPointIdx != hoverPoints[i]) {
        isDirty = true;
      }

      hoverPoints[i] = pointExists(nearestPointIdx) ? nearestPointIdx
          : NO_SELECTION;

      if (nearestHoverPt.dist <= closenessThreshold) {
        isCloseToCurve = true;
      }
    }

    if (isDirty) {
      redraw();
    }

    return isCloseToCurve;
  }

  public void setLegendEnabled(boolean b) {
    legendEnabled = b;
  }

  public void setOverviewEnabled(boolean overviewEnabled) {
    this.overviewEnabled = overviewEnabled;
  }

  public void setRenderer(int datasetIndex, XYRenderer r) {
    xyRenderers[datasetIndex] = r;
  }

  public double windowXtoUser(double x) {
    return (x - plotBounds.x) / plotBounds.width;
  }

  public void zoomToHighlight() {
    final double newOrigin = beginHighlight;
    double newdomain = endHighlight - beginHighlight;
    pushHistory();
    animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
  }

  private RangeAxis[] autoAssignDatasetAxes() {
    RangeAxis[] rangeAxes = new RangeAxis[datasets.size()];
    
    int rangeAxisCount = 0;
    for (int i = 0; i < datasets.size(); i++) {
      XYDataset ds = datasets.get(i);
      RangeAxis ra = (RangeAxis) id2rangeAxis.get(ds.getAxisId());
      if (ra == null) {
        AxisPanel currRangePanel = ((rangeAxisCount++) % 2 == 0)
            ? rangePanelLeft : rangePanelRight;
        ra = new RangeAxis(chart, ds.getRangeLabel(), ds.getAxisId(), i,
            ds.getRangeBottom(), ds.getRangeTop(), currRangePanel);
        id2rangeAxis.put(ra.getAxisId(), ra);
        currRangePanel.add(ra);
      } else {
        ra.setInitialRange(Math.min(ra.getUnadjustedRangeLow(), ds.getRangeBottom()),
            Math.max(ra.getUnadjustedRangeHigh(), ds.getRangeTop()));
      }
      rangeAxes[i] = ra;
    }
    
    return rangeAxes;
  }

  private void drawHighlight(Layer layer) {
    final double domainStart = plotDomain.getStart();
    final double domainEnd = plotDomain.getEnd();
    
    if (endHighlight - beginHighlight == 0
        || (beginHighlight < domainStart && endHighlight < domainStart) || (
        beginHighlight > domainEnd && endHighlight > domainEnd)) {
      
      if (highlightDrawn) {
        layer.clear();
        highlightDrawn = false;
      }
      
      return;
    }

    // need plotBounds relative
    double ux = Math.max(0, domainToScreenX(beginHighlight, 0));
    double ex = Math
        .min(0 + getInnerBounds().width, domainToScreenX(endHighlight, 0));

    layer.save();
    layer.setFillColor("#14FFFF");
    // layer.setLayerAlpha(0.2f);
    layer.setTransparency(0.2f);
    layer.clear();
    layer.fillRect(ux, 0, ex - ux, getInnerBounds().height);
    layer.restore();
    highlightDrawn = true;
  }

  private void pushHistory() {
    Chronoscope.pushHistory();
  }

  private void clearDrawCaches() {
    drewVertical = false;
    overviewDrawn = false;
  }

  private Bounds computePlotBounds() {
    final double viewWidth = view.getWidth();
    final double viewHeight = view.getHeight();
    Bounds b = new Bounds();

    // TODO: only in snapshot
    if (interactive) {
      double centerPlotHeight = 
          viewHeight - topPanel.getHeight() - domainPanel.getHeight();
      
      // If center plot too squished, remove the overview axis
      if (centerPlotHeight < MIN_PLOT_HEIGHT) {
        if (overviewEnabled) {
          domainPanel.remove(overviewAxis);
          overviewEnabled = false;
          centerPlotHeight = 
              viewHeight - topPanel.getHeight() - domainPanel.getHeight();
        }
      }

      // If center plot still too squished, remove the legend axis
      if (centerPlotHeight < MIN_PLOT_HEIGHT) {
        if (legendEnabled) {
          topPanel.remove(legendAxis);
          legendEnabled = false;
          centerPlotHeight = 
              viewHeight - topPanel.getHeight() - domainPanel.getHeight();
        }
      }
      
      b.x = rangePanelLeft.getWidth();
      b.y = topPanel.getHeight();
      b.height = centerPlotHeight;
      b.width = 
          viewWidth - rangePanelLeft.getWidth() - rangePanelRight.getWidth(); 
    }
    else {
      b.x = 0;
      b.y = 0;
      b.height = viewHeight;
      b.width = viewWidth;
    }

    return b;
  }

  private void drawOverlays(Layer layer) {
    layer.save();
    layer.clearTextLayer("overlays");
    layer.setTextLayerBounds("overlays", new Bounds(0, 0,
        layer.getBounds().width, layer.getBounds().height));

    for (Overlay o : overlays) {
      o.draw(layer, "overlays");
    }

    layer.restore();
  }
  
  /**
   * Draws the highlighted region (if any) of the dataset overview axis.
   */
  private void drawOverviewHighlight() {
    domainLayer.save();
    Bounds domainPanelBounds = new Bounds(plotBounds.x, 0, plotBounds.width,
        domainBounds.height);
    domainPanel.drawAxisPanel(this, domainLayer, domainPanelBounds, false);
    domainLayer.restore();
  }
  
  /**
   * Draws the overview (the  miniaturized fully-zoomed-out-view) of all 
   * the datasets managed by this plot.
   */
  private void drawOverviewOfDatasets() {
    double dO = plotDomain.getStart();
    double dE = plotDomain.getEnd();
    plotDomain.setEndpoints(datasets.getMinDomain(), datasets.getMaxDomain());
    
    drawPlot();
    
    overviewLayer.save();
    overviewLayer.setVisibility(false);
    overviewLayer.clear();
    overviewLayer.drawImage(plotLayer, 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight());
    overviewLayer.restore();

    plotDomain.setEndpoints(dO, dE);
  }
  
  private void drawPlot() {
    final double doChange = plotDomain.getStart() - lastPlotDomain.getStart();
    final double cdChange = plotDomain.length() - lastPlotDomain.length();

    double numPixels = doChange / plotDomain.length() * plotLayer.getWidth();
    // disabled for now, implement smooth local scrolling by rendering
    // a chart with overdraw clipped to the view, and scroll overdraw
    // regions into view
    // as needed
    if (false && cdChange == 0 && numPixels < plotLayer.getWidth() / 2) {
      plotLayer.setScrollLeft((int) (plotLayer.getScrollLeft() - numPixels));
      this.movePlotDomain(plotDomain.getStart() + doChange);
    } else {
      plotLayer.setScrollLeft(0);
      background.paint(this, plotLayer, plotDomain.getStart(), plotDomain.length());

      // reset the visible RangeAxis ticks if it's been zoomed
      for (RangeAxis axis : rangeAxes) {
        axis.initVisibleRange();
      }

      plotRenderer.drawDatasets();
    }
  }

  private Interval fenceDomain(double destDomainOrig, double destDomainLength) {
    final double minDomain = datasets.getMinDomain();
    final double maxDomain = datasets.getMaxDomain();
    final double maxDomainLength = maxDomain - minDomain;
    final double minTickSize = domainAxis.getMinimumTickSize();
    
    // First ensure that the destination domain length is smaller than the 
    // difference between the minimum and maximum dataset domain values.  
    // Then ensure that the destDomain is larger than what the DateAxis thinks
    //is it's smallest tick interval it can handle.
    final double fencedDomainLength =
        Math.max(Math.min(destDomainLength, maxDomainLength), minTickSize);
   
    double d = destDomainOrig;
    // if destDomainLength was bigger than entire date range of dataset
    // we set the domainOrigin to be the beginning of the dataset range
    if (destDomainLength >= maxDomainLength) {
      d = minDomain;
    } else {
      // else, our domain range is smaller than the max check to see if 
      // our origin is smaller than the smallest date range
      if (destDomainOrig < minDomain) {
        // and force it to be the min dataset range value
        d = minDomain;
      } else if (destDomainOrig + destDomainLength > maxDomain) {
        // we we check if the right side of the domain window
        // is past the maximum dataset date range value
        // and if it is, we place the domain origin so that the entire
        // chart fits perfectly in view
        d = maxDomain - destDomainLength;
      }
    }
    
    final double fencedDomainOrigin = d;
    return new Interval(fencedDomainOrigin, fencedDomainOrigin + fencedDomainLength);
  }
  
  /**
   * Finds the data point on a given dataset whose location is closest to the
   * specified (dataX, dataY) location.  This method modifies the fields in the
   * input argument <tt>np</tt>.
   *
   * @param dataX        - the domain value in data space
   * @param dataY        - the range value in data space
   * @param datasetIndex - the 0-based index of a dataset
   * @param df           - determines which distance formula to use when
   *                     determining the "closeness" of 2 points.
   * @param np           - result object that represents the point nearest to
   *                     (dataX, dataY).
   */
  private void findNearestPt(double dataX, double dataY, int datasetIndex,
      DistanceFormula df, NearestPoint np) {

    XYDataset ds = datasets.get(datasetIndex);
    int currMipLevel = currentMiplevels[datasetIndex];

    // Find index of data point closest to the right of dataX at the current MIP level
    int closestPtToRight = Util.binarySearch(ds, dataX, currMipLevel);

    double sx = domainToScreenX(dataX, datasetIndex);
    double sy = rangeToScreenY(dataY, datasetIndex);
    double rx = domainToScreenX(ds.getX(closestPtToRight, currMipLevel),
        datasetIndex);
    double ry = rangeToScreenY(ds.getY(closestPtToRight, currMipLevel),
        datasetIndex);

    int nearestHoverPt;
    if (closestPtToRight == 0) {
      nearestHoverPt = closestPtToRight;
      np.dist = df.dist(sx, sy, rx, ry);
    } else {
      int closestPtToLeft = closestPtToRight - 1;
      double lx = domainToScreenX(ds.getX(closestPtToLeft, currMipLevel),
          datasetIndex);
      double ly = rangeToScreenY(ds.getY(closestPtToLeft, currMipLevel),
          datasetIndex);
      double lDist = df.dist(sx, sy, lx, ly);
      double rDist = df.dist(sx, sy, rx, ry);

      if (lDist <= rDist) {
        nearestHoverPt = closestPtToLeft;
        np.dist = lDist;
      } else {
        nearestHoverPt = closestPtToRight;
        np.dist = rDist;
      }
    }

    np.pointIndex = nearestHoverPt;
  }

  private void initMipLevels() {
    currentMiplevels = new int[datasets.size()];
    Arrays.fill(currentMiplevels, 0);
  }

  private void initDefaultRenderers() {
    for (int i = 0; i < xyRenderers.length; i++) {
      if (xyRenderers[i] == null) {
        xyRenderers[i] = new XYLineRenderer(i);
      }
    }
  }

  private void initializeDomain() {
    plotDomain = new Interval(datasets.getMinDomain(), datasets.getMaxDomain());
  }

  private Layer initLayer(Layer layer, String layerPrefix, Bounds layerBounds) {
    Canvas canvas = view.getCanvas();
    if (layer != null) {
      canvas.disposeLayer(layer);
    }
    return canvas.createLayer(layerPrefix + plotNumber, layerBounds);
  }
  
  private void initLayers() {
    view.getCanvas().getRootLayer().setLayerOrder(Layer.Z_LAYER_BACKGROUND);

    plotLayer = initLayer(plotLayer, "plotLayer", plotBounds);
    
    if (interactive) {
      if (overviewEnabled) {
        overviewLayer = initLayer(overviewLayer, "overviewLayer", plotBounds);
        overviewLayer.setVisibility(false);
      }

      topBounds = new Bounds(0, 0, view.getWidth(), topPanel.getHeight());
      topLayer = initLayer(topLayer, "topLayer", topBounds);
      topLayer.setLayerOrder(Layer.Z_LAYER_AXIS);

      Bounds verticalAxisLayerBounds = new Bounds(0, plotBounds.y,
          view.getWidth(), rangePanelLeft.getHeight());
      verticalAxisLayer = initLayer(verticalAxisLayer, "verticalAxis", 
          verticalAxisLayerBounds);
      verticalAxisLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
      verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
      verticalAxisLayer.clear();

      domainBounds = new Bounds(0, plotBounds.bottomY(),
          view.getWidth(), domainPanel.getHeight());
      domainLayer = initLayer(domainLayer, "domainAxis", domainBounds);
      domainLayer.setLayerOrder(Layer.Z_LAYER_AXIS);

      highLightLayer = initLayer(highLightLayer, "highlight", plotBounds);
      highLightLayer.setLayerOrder(Layer.Z_LAYER_HIGHLIGHT);
    }
  }

  /**
   * Methods which do not depend on any visual state of the chart being
   * initialized first. Can be moved early in Plot initialization. Put stuff
   * here that doesn't depend on the axes or layers being initialized.
   */
  private void initViewIndependent() {
    visibleDomainMax = Util.calcVisibleDomainMax(getMaxDrawableDataPoints(), datasets);
    initializeDomain();
    initDefaultRenderers();
    initMipLevels();
  }

  /**
   * Returns true only if this plot is in a state such that animations
   * (e.g. zoom in, pan) are possible.
   */
  private boolean isAnimatable() {
    return this.plotDomain.length() != 0.0;
  }

  private void maxZoomToPoint(int pointIndex, int datasetIndex) {
    pushHistory();

    XYDataset dataset = datasets.get(datasetIndex);
    pointIndex = Util.binarySearch(dataset,
        dataset.getX(pointIndex, currentMiplevels[datasetIndex]), 0);

    final int zoomOffset = 10;
    final double newOrigin = dataset.getX(Math.max(0, pointIndex - zoomOffset));
    final double newdomain =
        dataset.getX(Math.min(dataset.getNumSamples(), pointIndex + zoomOffset))
            - newOrigin;

    animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
  }

  private void page(double pageSize) {
    pushHistory();
    final double newOrigin = plotDomain.getStart() + (plotDomain.length() * pageSize);
    animateTo(newOrigin, plotDomain.length(), XYPlotListener.PAGED);
  }

  /**
   * Assigns a new domain start value while maintaining the current domain length
   * (i.e. the domain end value is implicitly modified).
   */
  private void movePlotDomain(double newDomainStart) {
    double len = this.plotDomain.length();
    this.plotDomain.setEndpoints(newDomainStart, newDomainStart + len);
  }
  
  private void initAndRedraw() {
    init(this.view);
    redraw();
  }
  
  private void setFocusAndNotifyView(Focus focus) {
    if (focus == null) {
      this.focus = null;
      view.fireFocusEvent(this, NO_SELECTION, NO_SELECTION);
    } else {
      setFocusAndNotifyView(focus.getDatasetIndex(), focus.getPointIndex());
    }
  }

  private void setFocusAndNotifyView(int datasetIndex, int pointIndex) {
    if (this.focus == null) {
      this.focus = new Focus();
    }
    this.focus.setDatasetIndex(datasetIndex);
    this.focus.setPointIndex(pointIndex);

    view.fireFocusEvent(this, datasetIndex, pointIndex);
  }

  /**
   * Shifts the focus point <tt>n</tt> data points forward or backwards (e.g. a
   * value of <tt>+1</tt> moves the focus point forward, and a value of
   * <tt>-1</tt> moves the focus point backwards).
   */
  private void shiftFocus(int n) {
    if (n == 0) {
      return; // shift focus 0 data points left/right -- that was easy.
    }

    XYDataset ds;
    int focusDataset, focusPoint;
    int mipLevel;

    if (focus == null) {
      // If no data point currently has the focus, then set the focus point to
      // the point on dataset [0] that's closest to the center of the screen.
      focusDataset = 0;
      ds = datasets.get(focusDataset);
      mipLevel = currentMiplevels[focusDataset];
      double domainCenter = plotDomain.midpoint();
      focusPoint = Util.binarySearch(ds, domainCenter, mipLevel);
    } else {
      // some data point currently has the focus.
      focusDataset = focus.getDatasetIndex();
      focusPoint = focus.getPointIndex();
      mipLevel = currentMiplevels[focusDataset];
      focusPoint += n;

      if (focusPoint >= datasets.get(focusDataset).getNumSamples(mipLevel)) {
        ++focusDataset;
        if (focusDataset >= datasets.size()) {
          focusDataset = 0;
        }
        focusPoint = 0;
      } else if (focusPoint < 0) {
        --focusDataset;
        if (focusDataset < 0) {
          focusDataset = datasets.size() - 1;
        }
        focusPoint = datasets.get(focusDataset).getNumSamples(mipLevel) - 1;
      }

      ds = datasets.get(focusDataset);
    }

    double dataX = ds.getX(focusPoint, mipLevel);
    double dataY = ds.getY(focusPoint, mipLevel);
    ensureVisible(dataX, dataY, null);
    setFocusAndNotifyView(focusDataset, focusPoint);
    redraw();
  }

  private double windowYtoUser(int y) {
    return (plotBounds.height - (y - plotBounds.y)) / plotBounds.height;
  }

  private void resetHoverPoints() {
    Arrays.fill(this.hoverPoints, NO_SELECTION);
  }

  private static boolean pointExists(int pointIndex) {
    return pointIndex > NO_SELECTION;
  }

  /**
   * Render the Plot into the encapsulating Chart's View.
   */
  private void update() {
    Canvas backingCanvas = view.getCanvas();
    backingCanvas.beginFrame();

    plotLayer.save();
    plotLayer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
    plotLayer.clear();
    // plotLayer.setFillColor("#FF0000");
    // plotLayer.fillRect(0, 0, 50, 50);

    if (interactive) {
        
      if (!overviewDrawn && overviewEnabled) {
        drawOverviewOfDatasets();
        overviewDrawn = true;
      }
      
      if (domainPanel.getAxisCount() > 0) {
        drawOverviewHighlight();
      }

      boolean drawVertical = !drewVertical;
      for (RangeAxis axis : rangeAxes) {
        drawVertical = drawVertical || axis.isAutoZoomVisibleRange();
      }

      if (drawVertical) {
        verticalAxisLayer.save();
        verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
        verticalAxisLayer.clear();

        Bounds leftPanelBounds = new Bounds(0, 0, rangePanelLeft.getWidth(),
            rangePanelLeft.getHeight());
        rangePanelLeft
            .drawAxisPanel(this, verticalAxisLayer, leftPanelBounds, false);

        if (rangePanelRight.getAxisCount() > 0) {
          Bounds rightPanelBounds = new Bounds(plotBounds.rightX(), 0, 
              rangePanelRight.getWidth(), rangePanelRight.getHeight());
          rangePanelRight
              .drawAxisPanel(this, verticalAxisLayer, rightPanelBounds, false);
        }
        drewVertical = true;
        verticalAxisLayer.restore();
      }

      if (topPanel.getAxisCount() > 0) {
        topLayer.save();
        Bounds topPanelBounds = new Bounds(0, 0, view.getWidth(),
            topBounds.height);
        topPanel.drawAxisPanel(this, topLayer, topPanelBounds, false);
        topLayer.restore();
      }
    }
    
    drawPlot();
    drawOverlays(plotLayer);
    drawHighlight(highLightLayer);
    
    plotLayer.restore();
    backingCanvas.endFrame();
  }

  private double windowXtoDomain(double x) {
    return domainAxis.userToData(windowXtoUser(x));
  }

  private double windowYtoRange(int y, int datasetIndex) {
    return getRangeAxis(datasetIndex).userToData(windowYtoUser(y));
  }
  
  /**
   * Represents the point nearest to some specified data point.
   */
  private static final class NearestPoint {

    public int pointIndex;

    public double dist;

    public String toString() {
      return "pointIndex=" + pointIndex + ";dist=" + dist;
    }
  }

}
