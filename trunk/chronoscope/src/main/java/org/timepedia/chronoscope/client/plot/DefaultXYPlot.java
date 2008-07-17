package org.timepedia.chronoscope.client.plot;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.AxisPanel;
import org.timepedia.chronoscope.client.axis.DateAxis;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.StockMarketDateAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.HasRegions;
import org.timepedia.chronoscope.client.data.RegionLoadListener;
import org.timepedia.chronoscope.client.data.UpdateableXYDataset;
import org.timepedia.chronoscope.client.data.XYDatasetListener;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.render.Background;
import org.timepedia.chronoscope.client.render.GssBackground;
import org.timepedia.chronoscope.client.render.ScalableXYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYLineRenderer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYRenderer;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.Nearest;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
public class DefaultXYPlot
    implements XYPlot, Exportable, XYDatasetListener, RegionLoadListener {

  public static int MAX_DRAWABLE_DATAPOINTS = 400;

  public static final double ZOOM_FACTOR = 1.50d;

  private static final int FRAMES = 8;

  private static int globalPlotNumber = 0;

  private static final double MIN_PLOT_HEIGHT = 50;

  /**
   * Determines if a and b are equal, taking into consideration that a or b (or
   * both a and b) could be null.
   *
   * TODO: Move this into a utility class
   */
  private static boolean isEqual(Object a, Object b) {
    if (a == b) {
      return true;
    }

    if (a == null && b == null) {
      return true;
    }

    if ((a == null && b != null) || (b == null && a != null)) {
      return false;
    }

    return a.equals(b);
  }

  protected RangeAxis[] axes;

  protected Background background;

  protected double currentDomain;

  protected int currentMiplevels[];

  protected XYDataset[] datasets = null;

  protected DateAxis domainAxis;

  protected double domainEnd;

  protected double domainMin, domainMax;

  protected double domainOrigin;

  protected AxisPanel domainPanel;

  protected double domainStart;

  protected boolean drewVertical;

  protected int hoverPoint = -1;

  protected int hoverSeries = -1;

  protected final boolean interactive;

  protected final Nearest nearestSingleton = new Nearest();

  protected boolean overviewEnabled = true;

  protected Layer overviewLayer;

  protected Bounds plotBounds;

  protected AxisPanel rangePanelLeft;

  protected boolean selection;

  protected int selEnd;

  protected int selStart;

  protected final XYRenderer[] xyRenderers;

  private PortableTimerTask animationContinuation;

  private PortableTimer animationTimer;

  private final HashMap<String, RangeAxis> axisMap
      = new HashMap<String, RangeAxis>();

  private double beginHighlight = Double.MIN_VALUE;

  private Chart chart;

  private boolean domainAxisVisible = true;

  private Bounds domainBounds;

  private Layer domainLayer;

  private double endHighlight = Double.MIN_VALUE;

  private Focus focus = null;

  private boolean highlightDrawn;

  private Layer highLightLayer;

  private Bounds initialBounds;

  private Bounds innerBounds;

  private boolean isAnimating = false;

  private double lastCurrentDomain;

  private double lastDomainOrigin;

  private LegendAxis legendAxis;

  private ArrayList overlays;

  private OverviewAxis overviewAxis;

  private boolean overviewDrawn = false;

  private Layer plotLayer;

  private int plotNumber = 0;

  private XYPlotRenderer plotRenderer;

  private AxisPanel rangePanelRight;

  private boolean selectionMode;

  private boolean showLegend = true;

  private Bounds topBounds;

  private Layer topLayer;

  private AxisPanel topPanel;

  private Layer verticalAxisLayer;

  private View view;

  public DefaultXYPlot(Chart chart, XYDataset[] datasets, boolean interactive) {
    this(chart, datasets, interactive, null);
  }

  public DefaultXYPlot(Chart chart, XYDataset[] ds, boolean interactive,
      Bounds initialBounds) {
    this.chart = chart;
    this.datasets = ds;
    this.interactive = interactive;

    MAX_DRAWABLE_DATAPOINTS = 100 / ds.length;
    overlays = new ArrayList();
    xyRenderers = new XYRenderer[datasets.length];
    // computeVisibleDomainStartEnd();
    // initializeDomain();

    plotRenderer = new ScalableXYPlotRenderer(this);
    this.initialBounds = initialBounds;
    plotNumber = globalPlotNumber++;
    setupDatasetListeners();
  }

  /**
   * @gwt.export
   */
  @Export
  public void addOverlay(Overlay over) {

    overlays.add(over);
    over.setPlot(this);
  }

  public void animateTo(final double destinationOrigin,
      final double destinationDomain, final int eventType) {
    animateTo(destinationOrigin, destinationDomain, eventType, null);
  }

  public void animateTo(final double destinationOrigin,
      final double destinationDomain, final int eventType,
      final PortableTimerTask continuation) {
    animateTo(destinationOrigin, destinationDomain, eventType, continuation,
        true);
  }

  public void animateTo(final double destinationOrigin,
      final double destinationDomain, final int eventType,
      final PortableTimerTask continuation, boolean fence) {

    // if there is already an animation running, cancel it
    if (animationTimer != null) {
      animationTimer.cancelTimer();
      if (animationContinuation != null) {
        animationContinuation.run(animationTimer);
      }
      animationTimer = null;
    }
    final double fencedDomain = fenceDomain(fence, destinationDomain);
    final double fencedDomainOrigin = fenceDomainOrigin(fence,
        destinationOrigin, destinationDomain);

    animationContinuation = continuation;

    animationTimer = view.createTimer(new PortableTimerTask() {

      // destination center
      final double destCenter = fencedDomainOrigin + fencedDomain / 2;

      // center of current domain, we want the zoom to keep the center
      // point
      // stable
      double domainCenter = domainOrigin + currentDomain / 2;

      boolean lastFrame = false;

      // starting center point
      final double sCenter = domainCenter;

      final double startDomain = currentDomain;

      final double startOrigin = domainOrigin;

      double startTime = 0;

      // zoom factor, ratio of destination domain to current domain
      final double zf = fencedDomain / currentDomain;

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
        setCurrentDomain(startDomain * ((1 - lerpFactor) + zf * lerpFactor));
        domainCenter = (destCenter - sCenter) * lerpFactor + sCenter;
        domainOrigin = domainCenter - currentDomain / 2;
        redraw();

        if (lerpFactor < 1) {
          t.schedule(10);
        } else if (!lastFrame) {
          lastFrame = true;
          animationTimer.schedule(300);
        } else if (lastFrame) {
          view.fireScrollEvent(DefaultXYPlot.this, startOrigin - domainOrigin,
              0, eventType, false);
          if (continuation != null) {
            continuation.run(t);
            animationContinuation = null;
          }
          isAnimating = false;
          animationTimer = null;
          redraw();
        }
      }
    });

    animationTimer.schedule(10);
  }

  public void clearSelection() {
    selection = false;
    selStart = -1;
    selEnd = -1;
  }

  public boolean click(int x, int y) {
    if (setFocusXY(x, y)) {
      return true;
    } else {
      Iterator i = overlays.iterator();
      while (i.hasNext()) {
        Overlay o = (Overlay) i.next();
        double oPos = o.getDomainX();
        if (MathUtil
            .isBounded(oPos, domainOrigin, domainOrigin + currentDomain)) {
          if (o.isHit(x, y)) {
            o.click(x, y);
            return true;
          }
        }
      }
    }
    return showLegend ? legendAxis.click(x, y) : false;
  }

  /**
   * Any cached drawings of this axis are flushed and redrawn on next update
   */
  public void damageAxes(ValueAxis axis) {
    drewVertical = false;
  }

  public double domainToScreenX(double dataX, int datasetIndex) {
    return userToScreenX(getDomainAxis().dataToUser(dataX));
  }

  public double domainToWindowX(double dataX, int datasetIndex) {
    return userToWindowX(getDomainAxis().dataToUser(dataX));
  }

  /**
   * @gwt.export
   */
  @Export
  public boolean ensureVisible(final double domainX, final double rangeY,
      PortableTimerTask callback) {
    view.ensureViewVisible();
    if (domainX <= domainOrigin || domainX >= domainOrigin + currentDomain) {
      scrollAndCenter(domainX, callback);
      return true;
    }
    return false;
  }

  public boolean ensureVisible(int datasetIndex, int pointIndex,
      PortableTimerTask callback) {
    return ensureVisible(datasets[datasetIndex].getX(pointIndex),
        datasets[datasetIndex].getY(pointIndex), callback);
  }

  public Chart getChart() {
    return chart;
  }

  public int getCurrentDatasetLevel(int datasetIndex) {
    return currentMiplevels[datasetIndex];
  }

  public double getCurrentDomain() {
    return currentDomain;
  }

  public int getCurrentMipLevel(int datasetIndex) {
    return currentMiplevels[datasetIndex];
  }

  public XYDataset getDataset(int datasetIndex) {
    return datasets[datasetIndex];
  }

  public double getDataX(int datasetIndex, int pointIndex) {
    return datasets[datasetIndex]
        .getX(pointIndex, currentMiplevels[datasetIndex]);
  }

  public double getDataY(int datasetIndex, int pointIndex) {
    return datasets[datasetIndex]
        .getY(pointIndex, currentMiplevels[datasetIndex]);
  }

  public ValueAxis getDomainAxis() {
    return domainAxis;
  }

  public double getDomainCenter() {

    return domainOrigin + currentDomain / 2;
  }

  public double getDomainEnd() {
    return domainEnd;
  }

  public double getDomainMax() {
    return domainMax;
  }

  public double getDomainMin() {
    return domainMin;
  }

  public double getDomainOrigin() {
    return domainOrigin;
  }

  public double getDomainStart() {
    return domainStart;
  }

  public Focus getFocus() {
    return this.focus;
  }

  public String getHistoryToken() {
    return getChart().getChartId() + "(O" + getDomainOrigin() + ",D"
        + getCurrentDomain() + ")";
  }

  public int getHoverPoint() {
    return hoverPoint;
  }

  public int getHoverSeries() {
    return hoverSeries;
  }

  public Bounds getInnerPlotBounds() {
    return innerBounds;
  }

  public int getMaxDrawableDataPoints() {
    return (int) (isAnimating ? MAX_DRAWABLE_DATAPOINTS : 1000);
  }

  public int getNearestVisiblePoint(double domainX, int series) {
    return Util
        .binarySearch(datasets[series], domainX, currentMiplevels[series]);
  }

  public int getNumAnimationFrames() {
    return FRAMES;
  }

  public int getNumDatasets() {
    return datasets == null ? 0 : datasets.length;
  }

  public OverviewAxis getOverviewAxis() {
    return overviewAxis;
  }

  public Bounds getOverviewBounds() {
    return overviewAxis.getBounds();
  }

  public Layer getOverviewLayer() {
    return overviewLayer;
  }

  public Bounds getPlotBounds() {
    return plotBounds;
  }

  public XYPlot getPlotForAxis(ValueAxis theAxis) {
    return this;
  }

  public Layer getPlotLayer() {
    return view.getCanvas().createLayer("plotLayer" + plotNumber, plotBounds);
  }

  /**
   * @gwt.export getAxis
   */
  @Export("getAxis")
  public RangeAxis getRangeAxis(int datasetIndex) {
    return axes[datasetIndex];
  }

  public int getRangeAxisCount() {
    return axisMap.size();
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

  public int getSeriesCount() {
    return datasets.length;
  }

  public String getSeriesLabel(int i) {
    return datasets[i].getRangeLabel() + getRangeAxis(i)
        .getLabelSuffix(getRangeAxis(i).getRange());
  }

  public boolean hasAxis(ValueAxis theAxis) {
    return topPanel.contains(theAxis) || domainPanel.contains(theAxis)
        || rangePanelLeft.contains(theAxis) || rangePanelRight
        .contains(theAxis);
  }

  public void init(View view) {
    this.view = view;
    this.focus = null;

    initViewIndependent();

    this.view.getCanvas().getRootLayer().setVisibility(true);

    domainPanel = new AxisPanel("domainAxisLayer" + plotNumber,
        AxisPanel.BOTTOM);
    domainAxis = new StockMarketDateAxis(this, domainPanel);

    if (domainAxisVisible) {
      domainPanel.add(domainAxis);
    }

    if (overviewEnabled) {
      overviewAxis = new OverviewAxis(this, domainPanel, "Overview");
      domainPanel.add(overviewAxis);
    }

    axisMap.clear();

    rangePanelLeft = new AxisPanel("rangeAxisLayerLeft" + plotNumber,
        AxisPanel.LEFT);
    rangePanelRight = new AxisPanel("rangeAxisLayerRight" + plotNumber,
        AxisPanel.RIGHT);
    axes = new RangeAxis[datasets.length];

    autoAssignDatasetAxes();

    topPanel = new AxisPanel("topPanel" + plotNumber, AxisPanel.TOP);
    legendAxis = new LegendAxis(this, topPanel, "My graph");
    if (showLegend) {
      topPanel.add(legendAxis);
    }

    computePlotBounds();
    clearDrawCaches();

    lastDomainOrigin = domainOrigin;
    lastCurrentDomain = currentDomain;

    initLayers();
    background = new GssBackground(this);

    view.canvasSetupDone();
  }

  /**
   * Methods which do not depend on any visual state of the chart being
   * initialized first. Can be moved early in Plot initialization. Put stuff
   * here that doesn't depend on the axes or layers being initialized.
   */
  private void initViewIndependent() {
    computeDomainMinMax();
    computeVisibleDomainStartEnd();
    initializeDomain();
    initDefaultRenderers();
    initDatasetLevels();
  }

  public boolean isAnimating() {
    return isAnimating;
  }

  public boolean isDomainAxisVisible() {
    return domainAxisVisible;
  }

  public boolean isSelectionModeEnabled() {
    return selectionMode;
  }

  public void maxZoomOut() {
    pushHistory();
    animateTo(domainMin, domainMax - domainMin, XYPlotListener.ZOOMED);
  }

  public boolean maxZoomTo(int x, int y) {

    int nearPointIndex = -1;
    int nearDataSetIndex = 0;
    double nearDist = Double.MAX_VALUE;

    for (int i = 0; i < datasets.length; i++) {
      double domainX = windowXtoDomain(x, i);
      double rangeY = windowYtoRange(y, i);
      Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i,
          10);
      if (nearest.nearest > -1 && nearest.dist < nearDist) {
        nearPointIndex = nearest.nearest;
        nearDataSetIndex = nearest.series;
        nearDist = nearest.dist;
      }
    }

    if (nearPointIndex >= 0) {
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

  public void nextFocus() {
    if (focus == null) {
      setFocusAndNotifyView(0, 0);
    } else {
      int focusSeries = this.focus.getDatasetIndex();
      int focusPoint = this.focus.getPointIndex();
      focusPoint++;
      if (focusPoint >= datasets[focusSeries]
          .getNumSamples(currentMiplevels[focusSeries])) {
        focusPoint = 0;
        focusSeries++;
        if (focusSeries >= datasets.length) {
          focusSeries = 0;
        }
      }
      int currentMip = currentMiplevels[focusSeries];
      ensureVisible(datasets[focusSeries].getX(focusPoint, currentMip),
          datasets[focusSeries].getY(focusPoint, currentMip), null);
      setFocusAndNotifyView(focusSeries, focusPoint);
    }

    redraw();
  }

  public void nextZoom() {
    pushHistory();
    double nDomain = currentDomain / ZOOM_FACTOR;
    animateTo(getDomainCenter() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
  }

  public void onDatasetChanged(XYDataset dataset, double domainStart,
      double domainEnd) {
    computeDomainMinMax();
    computeVisibleDomainStartEnd();
    damageAxes(getRangeAxis(findIndexForDataSet(dataset)));
    if (domainEnd > domainOrigin + currentDomain) {
      animateTo(domainEnd - currentDomain / 2, currentDomain, 0,
          new PortableTimerTask() {
            public void run(PortableTimer timer) {
              overviewDrawn = false;
              redraw();
            }
          }, false);
    } else {
      overviewDrawn = false;
      redraw();
    }
  }

  public void onRegionLoaded(HasRegions h, int regionNumber) {
    GWT.log("redrawing due to region load", null);
    redraw();
  }

  public void openInfoWindow(final String html, final double domainX,
      final double rangeY, final int datasetIndex) {

    if (ensureVisible(domainX, rangeY, new PortableTimerTask() {

      public void run(PortableTimer timer) {
        view.openInfoWindow(html,
            chart.domainToWindowX(DefaultXYPlot.this, domainX, datasetIndex),
            chart.rangeToWindowY(DefaultXYPlot.this, rangeY, datasetIndex) + 5);
      }
    })) {

    } else {
      view.openInfoWindow(html,
          chart.domainToWindowX(DefaultXYPlot.this, domainX, datasetIndex),
          chart.rangeToWindowY(DefaultXYPlot.this, rangeY, datasetIndex) + 5);
    }
  }

  public void pageLeft(double pageSize) {
    pushHistory();

    final double newOrigin = domainOrigin - currentDomain * pageSize;
    animateTo(newOrigin, currentDomain, XYPlotListener.PAGED);
  }

  public void pageRight(double pageSize) {
    pushHistory();

    final double newOrigin = domainOrigin + currentDomain * pageSize;
    animateTo(newOrigin, currentDomain, XYPlotListener.PAGED);
  }

  public void prevFocus() {
    if (this.focus == null) {
      setFocusAndNotifyView(0, 0);
    } else {
      int focusSeries = this.focus.getDatasetIndex();
      int focusPoint = this.focus.getPointIndex();
      focusPoint--;
      if (focusPoint < 0) {
        focusSeries--;
        if (focusSeries < 0) {
          focusSeries = datasets.length - 1;
        }
        focusPoint =
            datasets[focusSeries].getNumSamples(currentMiplevels[focusSeries])
                - 1;
      }
      setFocusAndNotifyView(focusSeries, focusPoint);
    }
    redraw();
  }

  public void prevZoom() {

    pushHistory();
    double nDomain = currentDomain * ZOOM_FACTOR;
    animateTo(getDomainCenter() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
  }

  public double rangeToScreenY(double dataY, int datasetIndex) {
    return userToScreenY(getRangeAxis(datasetIndex).dataToUser(dataY));
  }

  public double rangeToWindowY(double dataY, int datasetIndex) {
    return userToWindowY(getRangeAxis(datasetIndex).dataToUser(dataY));
  }

  /**
   * @gwt.export
   */
  @Export
  public void redraw() {
    update();
    lastCurrentDomain = currentDomain;
    lastDomainOrigin = domainOrigin;
    view.flipCanvas();
  }

  /**
   * @gwt.export
   */
  @Export
  public void reloadStyles() {
    overviewDrawn = false;
    double so = getDomainOrigin();
    double scd = getCurrentDomain();
    init(view);
    ArrayList oldOverlays = overlays;
    overlays = new ArrayList();

    initializeDomain();
    redraw();
    setDomainOrigin(so);
    setCurrentDomain(scd);
    overlays = oldOverlays;
    redraw();
  }

  public void removeOverlay(Overlay over) {
    overlays.remove(over);
  }

  public void scrollAndCenter(double domainX, PortableTimerTask continuation) {
    pushHistory();

    final double newOrigin = domainX - currentDomain / 2;
    animateTo(newOrigin, currentDomain, XYPlotListener.CENTERED, continuation);
  }

  public void scrollPixels(int amt) {

    final double damt = (double) amt / plotBounds.width * currentDomain;

    domainOrigin += damt;
    if (domainOrigin + currentDomain > getDomainMax()) {
      domainOrigin = getDomainMax() - currentDomain;
    } else if (domainOrigin < getDomainMin()) {
      domainOrigin = getDomainMin();
    }

    redraw();
    view.fireScrollEvent(DefaultXYPlot.this, damt, 0, XYPlotListener.DRAGGED,
        false);
  }

  public void setAnimating(boolean animating) {
    this.isAnimating = animating;
  }

  public void setAutoZoomVisibleRange(int dataset, boolean autoZoom) {
    axes[dataset].setAutoZoomVisibleRange(autoZoom);
  }

  public void setAxisForDataset(RangeAxis ra, int datasetNum) {
    axes[datasetNum] = ra;
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  public void setCurrentDatasetLevel(int datasetIndex, int mipLevel) {
    if (currentMiplevels[datasetIndex] != mipLevel) {
      hoverPoint = -1;
      hoverSeries = -1;
      // TODO: maybe adjust to nearest one in next level of detail
      focus = null;
    }
    currentMiplevels[datasetIndex] = mipLevel;
  }

  public void setCurrentDomain(double currentDomain) {
    this.currentDomain = currentDomain;
  }

  public void setDataset(int i, XYDataset d) {
    datasets[i] = d;
    if (d instanceof UpdateableXYDataset) {
      UpdateableXYDataset ud = (UpdateableXYDataset) d;
      ud.addXYDatasetListener(this);
    }
    if (d instanceof HasRegions) {
      ((HasRegions) d).addRegionLoadListener(this);
    }
  }

  public void setDomainAxisVisible(boolean visible) {
    this.domainAxisVisible = visible;
  }

  public void setDomainOrigin(double domainOrigin) {
    this.domainOrigin = domainOrigin;
  }

  public void setFocus(Focus focus) {
    this.focus = focus;
  }

  public boolean setFocusXY(int x, int y) {
    Focus lastFocus = (focus == null) ? null : focus.copy();

    int nearNum = -1;
    int nearSer = 0;
    double nearDist = Double.MAX_VALUE;

    for (int i = 0; i < datasets.length; i++) {
      double domainX = windowXtoDomain(x, i);
      double rangeY = windowYtoRange(y, i);
      Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i,
          10);
      if (nearest.nearest > -1 && nearest.dist < nearDist) {
        nearNum = nearest.nearest;
        nearSer = nearest.series;
        nearDist = nearest.dist;
      }
    }
    if (nearNum >= 0) {
      if (nearSer == -1) {
        setFocusAndNotifyView(null);
      } else {
        setFocusAndNotifyView(nearSer, nearNum + 1);
      }

      if (!isEqual(lastFocus, focus)) {
        redraw();
      }
      return true;
    } else {

      setFocusAndNotifyView(null);
      if (!isEqual(lastFocus, focus)) {
        redraw();
      }
    }
    return false;
  }

  public void setHighlight(double begin, double end) {
    beginHighlight = begin;
    endHighlight = end;
  }

  public void setHighlight(int selStart, int selEnd) {

    int tmp = Math.min(selStart, selEnd);
    selEnd = Math.max(selStart, selEnd);
    selStart = tmp;
    beginHighlight = windowXtoDomain(selStart, 0);
    endHighlight = windowXtoDomain(selEnd, 0);
    redraw();
    // drawHighlight(highLightLayer, highLightLayer);
  }

  public boolean setHover(int x, int y) {

    int lastHover = hoverPoint;
    int lastHoverSeries = hoverSeries;
    int nearNum = -1;
    int nearSer = 0;
    double nearDist = Double.MAX_VALUE;

    for (int i = 0; i < datasets.length; i++) {
      double domainX = windowXtoDomain(x, i);
      double rangeY = windowYtoRange(y, i);
      Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i,
          15);
      if (nearest.nearest > -1 && nearest.dist < nearDist) {
        nearNum = nearest.nearest;
        nearSer = nearest.series;
        nearDist = nearest.dist;
      }
    }

    if (nearNum >= 0) {
      hoverPoint = nearNum + 1;
      hoverSeries = nearSer;
      if (lastHoverSeries != hoverSeries || lastHover != hoverPoint) {
        redraw();
      }
      return true;
    } else {
      hoverPoint = -1;
      if (lastHoverSeries != hoverSeries || lastHover != hoverPoint) {
        redraw();
      }
    }
    return false;
  }

  public void setInitialBounds(Bounds initialBounds) {
    this.initialBounds = initialBounds;
  }

  public void setLegendEnabled(boolean b) {
    showLegend = b;
  }

  public void setOverviewEnabled(boolean overviewEnabled) {
    this.overviewEnabled = overviewEnabled;
  }

  public void setRenderer(int datasetIndex, XYRenderer r) {
    xyRenderers[datasetIndex] = r;
  }

  public void setSelectionMode(boolean b) {
    this.selectionMode = b;
  }

  public void showOverview() {
  }

  public void update() {

    Canvas backingCanvas = view.getCanvas();
    backingCanvas.beginFrame();

    plotLayer.save();
    plotLayer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
    plotLayer.clear();
    // plotLayer.setFillColor("#FF0000");
    // plotLayer.fillRect(0, 0, 50, 50);

    if (interactive && !overviewDrawn && overviewEnabled) {
      double dO = domainOrigin;
      double cD = currentDomain;
      domainOrigin = getDomainMin();
      currentDomain = getDomainMax() - domainOrigin;
      drawPlot();
      overviewLayer.save();
      overviewLayer.setVisibility(false);
      overviewLayer
          .clearRect(0, 0, overviewLayer.getWidth(), overviewLayer.getHeight());

      overviewLayer.drawImage(plotLayer, 0, 0, overviewLayer.getWidth(),
          overviewLayer.getHeight());
      overviewDrawn = true;
      overviewLayer.restore();
      domainOrigin = dO;
      currentDomain = cD;
    }

    if (interactive) {

      boolean drawVertical = !drewVertical;
      for (int i = 0; i < axes.length; i++) {
        drawVertical = drawVertical || axes[i].isAutoZoomVisibleRange();
      }

      if (drawVertical) {
        verticalAxisLayer.save();
        verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
        verticalAxisLayer.clearRect(0, 0, verticalAxisLayer.getWidth(),
            verticalAxisLayer.getHeight());

        drawAxisPanel(verticalAxisLayer, rangePanelLeft, new Bounds(0, 0,
            rangePanelLeft.getWidth(), rangePanelLeft.getHeight()), false);
        if (rangePanelRight.getAxisCount() > 0) {
          Bounds rightBounds = new Bounds(plotBounds.x + plotBounds.width, 0,
              rangePanelRight.getWidth(), rangePanelRight.getHeight());

          drawAxisPanel(verticalAxisLayer, rangePanelRight, rightBounds, false);
        }
        drewVertical = true;
        verticalAxisLayer.restore();
      }

      if (domainAxisVisible && domainPanel.getAxisCount() > 0) {
        domainLayer.save();
        drawAxisPanel(domainLayer, domainPanel,
            new Bounds(plotBounds.x, 0, plotBounds.width, domainBounds.height),
            false);
        domainLayer.restore();
      }

      if (true && topPanel.getAxisCount() > 0) {

        topLayer.save();
        drawAxisPanel(topLayer, topPanel,
            new Bounds(0, 0, view.getViewWidth(), topBounds.height), false);
        topLayer.restore();
      }
    }
    drawPlot();
    drawOverlays(plotLayer);
    drawHighlight(highLightLayer);
    plotLayer.restore();
    backingCanvas.endFrame();
  }

  /**
   * Convert a value in user coordinates [0,1] to plot region screen-space
   * coordinates [0, plotBounds.width].
   */
  public double userToScreenX(double userX) {
    return userX * plotBounds.width;
  }

  public double userToScreenY(double userY) {
    return plotBounds.height - userY * plotBounds.height;
  }

  /**
   * COnvert a value in user coordinates [0,1] to window screen-space
   * coordinates [plotBounds.x, plotBounds.width]
   */
  public double userToWindowX(double userX) {
    return userToScreenX(userX) + plotBounds.x;
  }

  public double userToWindowY(double userY) {
    return userToScreenY(userY) + plotBounds.y;
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

  protected void autoAssignDatasetAxes() {
    int rangeAxisCount = -1;
    for (int i = 0; i < datasets.length; i++) {
      RangeAxis ra = (RangeAxis) axisMap.get(datasets[i].getAxisId());
      if (ra == null) {
        rangeAxisCount++;
        ra = new RangeAxis(chart, datasets[i].getRangeLabel(),
            datasets[i].getAxisId(), i, datasets[i].getRangeBottom(),
            datasets[i].getRangeTop(),
            rangeAxisCount % 2 == 0 ? rangePanelLeft : rangePanelRight);
        axisMap.put(ra.getAxisId(), ra);
        if (rangeAxisCount % 2 == 0) {
          rangePanelLeft.add(ra);
        } else {
          rangePanelRight.add(ra);
        }
      } else {
        ra.setRange(Math.min(ra.getRangeLow(), datasets[i].getRangeBottom()),
            Math.max(ra.getRangeHigh(), datasets[i].getRangeTop()));
      }

      axes[i] = ra;
    }
  }

  protected void drawAxisPanel(Layer backingCanvas, AxisPanel axisPanel,
      Bounds bounds, boolean gridOnly) {
    axisPanel.drawAxisPanel(this, backingCanvas, bounds, gridOnly);
  }

  protected void drawHighlight(Layer layer) {
    if (endHighlight - beginHighlight == 0
        || (beginHighlight < domainOrigin && endHighlight < domainOrigin) || (
        beginHighlight > domainOrigin + currentDomain
            && endHighlight > domainOrigin + currentDomain)) {
      if (highlightDrawn) {
        layer.clear();
        highlightDrawn = false;
      }
      return;
    }

    // need plotBounds relative
    double ux = Math.max(0, domainToScreenX(beginHighlight, 0));
    double ex = Math
        .min(0 + getInnerPlotBounds().width, domainToScreenX(endHighlight, 0));

    layer.save();
    layer.setFillColor("#14FFFF");
    // layer.setLayerAlpha(0.2f);
    layer.setTransparency(0.2f);
    layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
    layer.fillRect(ux, 0, ex - ux, getInnerPlotBounds().height);
    layer.restore();
    highlightDrawn = true;
  }

  protected void pushHistory() {
    Chronoscope.pushHistory();
  }

  protected double windowXtoDomain(double x, int datasetIndex) {
    return getDomainAxis().userToData(windowXtoUser(x));
  }

  protected double windowYtoRange(int y, int datasetIndex) {
    return getRangeAxis(datasetIndex).userToData(windowYtoUser(y));
  }

  private void clearDrawCaches() {
    drewVertical = false;
    overviewDrawn = false;
  }

  private void computeDomainMinMax() {
    domainMin = Double.POSITIVE_INFINITY;
    domainMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < datasets.length; i++) {
      double min = datasets[i].getDomainBegin();
      domainMin = Math.min(domainMin, min);
      double max = datasets[i].getDomainEnd();
      domainMax = Math.max(domainMax, max);
    }
  }

  private void computePlotBounds() {
    plotBounds = initialBounds == null ? new Bounds(0, 0,
        this.view.getViewWidth(), this.view.getViewHeight())
        : new Bounds(initialBounds);
    
    // TODO: this padding is a workaround.  Apparently, the height computed
    // for the main plot bounds does not take into consideration the highest
    // range character (which "sits on top of" the northern-most range axis tick.
    // Without this hardcoded padding, the highest range value within the
    // plot are encroaches on the southern-most dataset legend row.
    final double topPanelPad = 19;

    // TODO: only in snapshot
    if (interactive) {
      plotBounds.x = rangePanelLeft.getWidth();
      plotBounds.width -= plotBounds.x;
      plotBounds.y += topPanel.getHeight() + topPanelPad;
      if (domainAxisVisible && domainPanel.getAxisCount() > 0) {
        final double topHeight = topPanel.getHeight();
        double topBottomHeight = domainPanel.getHeight() + topHeight;
        if (plotBounds.height - topBottomHeight < MIN_PLOT_HEIGHT) {
          if (overviewEnabled) {
            domainPanel.remove(overviewAxis);
            overviewEnabled = false;
          }
          topBottomHeight = domainPanel.getHeight() + topPanel.getHeight();

          if (plotBounds.height - topBottomHeight < MIN_PLOT_HEIGHT) {
            topPanel.remove(legendAxis);
            showLegend = false;
            plotBounds.y -= topHeight;
          }
          topBottomHeight = domainPanel.getHeight() + topPanel.getHeight();
        }
        plotBounds.height -= topBottomHeight;
      }
      if (rangePanelRight.getAxisCount() > 0) {
        plotBounds.width -= rangePanelRight.getWidth();
      }
    }

    innerBounds = new Bounds(plotBounds);
    innerBounds.height = plotBounds.height;
    innerBounds.width = plotBounds.width;
    innerBounds.x = 0;
    innerBounds.y = 0;
  }

  private void computeVisibleDomainStartEnd() {
    domainStart = Util.computeDomainStart(this, datasets);
    domainEnd = Util.computeDomainEnd(this, datasets);
  }

  private double dist(double x1, double y1, double cx, double cy) {
    // TODO: we now ignore y dist to make hover easier
    return Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy));
    //return Math.abs(x1-cx);
  }

  private void drawOverlays(Layer overviewLayer) {
    overviewLayer.save();
    overviewLayer.clearTextLayer("overlays");
    overviewLayer.setTextLayerBounds("overlays", new Bounds(0, 0,
        overviewLayer.getBounds().width, overviewLayer.getBounds().height));
    Iterator i = overlays.iterator();

    char label = 'A';

    while (i.hasNext()) {
      Overlay o = (Overlay) i.next();
      double oPos = o.getDomainX();
      if (MathUtil
          .isBounded(oPos, domainOrigin, domainOrigin + currentDomain)) {
        if (o instanceof Marker) {
          Marker m = (Marker) o;
          m.setLabel("" + label);
          label++;
        }
      }
      o.draw(overviewLayer, "overlays");
    }

    overviewLayer.restore();
  }

  private void drawPlot() {
    double doChange = domainOrigin - lastDomainOrigin;
    double cdChange = currentDomain - lastCurrentDomain;

    double numPixels = doChange / currentDomain * plotLayer.getWidth();
    // disabled for now, implement smooth local scrolling by rendering
    // a chart with overdraw clipped to the view, and scroll overdraw
    // regions into view
    // as needed
    if (false && cdChange == 0 && numPixels < plotLayer.getWidth() / 2) {
      plotLayer.setScrollLeft((int) (plotLayer.getScrollLeft() - numPixels));
      domainOrigin += doChange;
    } else {
      plotLayer.setScrollLeft(0);

      background.paint(this, plotLayer, domainOrigin, currentDomain);

      // reset the visible RangeAxis ticks if it's been zoomed
      for (int i = 0; i < datasets.length; i++) {
        axes[i].initVisibleRange();
      }

      plotRenderer.drawDatasets();
    }
  }

  private double fenceDomain(boolean fence, double destinationDomain) {
    // first ensure that the destinationDomain is smaller than the
    // difference between the minimum and maximum dataset date values
    // then ensure that the destinationDomain is larger than what
    // the DateAxis thinks is it's smallest tick interval it can handle.
    return fence ? Math.max(
        Math.min(destinationDomain, getDomainMax() - getDomainMin()),
        getDomainAxis().getMinimumTickSize()) : destinationDomain;
  }

  private double fenceDomainOrigin(boolean fence, double destinationOrigin,
      double destinationDomain) {
    if (!fence) {
      return destinationOrigin;
    }

    double maxDomain = getDomainMax() - getDomainMin();
    double d = destinationOrigin;
    // if destinationDomain was bigger than entire date range of dataset
    // we set the domainOrigin to be the beginning of the dataset range
    if (destinationDomain >= maxDomain) {
      d = getDomainMin();
    } else if (destinationDomain < maxDomain) {
      // else, our domain range is smaller than the max
      // check to see if our origin is smaller than the smallest date
      // range
      if (destinationOrigin < getDomainMin()) {
        // and force it to be the min dataset range value
        d = getDomainMin();
      } else if (destinationOrigin + destinationDomain > getDomainMax()) {
        // we we check if the right side of the domain window
        // is past the maximum dataset date range value
        // and if it is, we place the domain origin so that the entire
        // chart
        // fits perfectly in view
        d = getDomainMax() - destinationDomain;
      }
    }
    return d;
  }

  private int findIndexForDataSet(XYDataset dataset) {
    for (int i = 0; i < datasets.length; i++) {
      if (datasets[i] == dataset) {
        return i;
      }
    }
    return 0; // TODO: silent fail
  }

  private Nearest findNearestWithin(Nearest nearestResult, double domainX,
      double rangeY, int datasetIndex, int within) {
    double cx = domainToScreenX(domainX, datasetIndex);
    double cy = rangeToScreenY(rangeY, datasetIndex);

    int where = Util.binarySearch(datasets[datasetIndex], domainX,
        currentMiplevels[datasetIndex]);

    double x1 = domainToScreenX(
        datasets[datasetIndex].getX(where, currentMiplevels[datasetIndex]),
        datasetIndex);
    double y1 = rangeToScreenY(
        datasets[datasetIndex].getY(where, currentMiplevels[datasetIndex]),
        datasetIndex);
    double x2, y2;
    if (where + 1 < datasets[datasetIndex]
        .getNumSamples(currentMiplevels[datasetIndex])) {
      x2 = domainToScreenX(datasets[datasetIndex].getX(where + 1,
          currentMiplevels[datasetIndex]), datasetIndex);
      y2 = rangeToScreenY(datasets[datasetIndex].getY(where + 1,
          currentMiplevels[datasetIndex]), datasetIndex);
    } else {
      x2 = x1;
      y2 = y1;
    }

    double d1 = dist(x1, y1, cx, cy);
    double d2 = dist(x2, y2, cx, cy);
    nearestResult.nearest = -1;
    nearestResult.series = datasetIndex;

    if (d1 <= d2) {
      if (d1 < within) {
        nearestResult.nearest = where - 1;
        nearestResult.dist = d1;
      }
    } else if (d2 < within) {
      nearestResult.nearest = where;
      nearestResult.dist = d2;
    }
    return nearestResult;
  }

  private void initDatasetLevels() {
    currentMiplevels = new int[datasets.length];
    for (int i = 0; i < currentMiplevels.length; i++) {
      currentMiplevels[i] = 0;
    }
  }

  private void initDefaultRenderers() {
    for (int i = 0; i < datasets.length; i++) {
      if (xyRenderers[i] == null) {
        xyRenderers[i] = new XYLineRenderer(i);
      }
    }
  }

  private void initializeDomain() {
    domainOrigin = domainMin;
    currentDomain = domainMax - domainOrigin;
  }

  private void initLayers() {
    Canvas backingCanvas = view.getCanvas();

    backingCanvas.getRootLayer().setLayerOrder(Layer.Z_LAYER_BACKGROUND);

    if (plotLayer != null) {
      backingCanvas.disposeLayer(plotLayer);
    }
    plotLayer = backingCanvas.createLayer("plotLayer" + plotNumber, plotBounds);

    if (interactive) {

      if (overviewEnabled) {
        if (overviewLayer != null) {
          backingCanvas.disposeLayer(overviewLayer);
        }

        overviewLayer = backingCanvas
            .createLayer("overviewLayer" + plotNumber, plotBounds);
        overviewLayer.setVisibility(false);
      }

      Bounds layerBounds = new Bounds(0, plotBounds.y, view.getViewWidth(),
          rangePanelLeft.getHeight());
      if (verticalAxisLayer != null) {
        backingCanvas.disposeLayer(verticalAxisLayer);
      }

      topBounds = new Bounds(0, 0, view.getViewWidth(), topPanel.getHeight());
      topLayer = backingCanvas.createLayer("topLayer" + plotNumber, topBounds);
      topLayer.setLayerOrder(Layer.Z_LAYER_AXIS);

      verticalAxisLayer = backingCanvas
          .createLayer("verticalAxis" + plotNumber, layerBounds);
      verticalAxisLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
      verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
      verticalAxisLayer.clearRect(0, 0, verticalAxisLayer.getWidth(),
          verticalAxisLayer.getHeight());

      domainBounds = new Bounds(0, plotBounds.y + plotBounds.height,
          view.getViewWidth(), domainPanel.getHeight());

      if (domainLayer != null) {
        backingCanvas.disposeLayer(domainLayer);
      }

      domainLayer = backingCanvas
          .createLayer("domainAxis" + plotNumber, domainBounds);
      domainLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
      highLightLayer = backingCanvas
          .createLayer("highlight" + plotNumber, plotBounds);
      highLightLayer.setLayerOrder(Layer.Z_LAYER_HIGHLIGHT);
    }
  }

  private void maxZoomToPoint(int pointIndex, int datasetIndex) {
    pushHistory();

    XYDataset dataset = datasets[datasetIndex];
    pointIndex = Util.binarySearch(dataset,
        dataset.getX(pointIndex, currentMiplevels[datasetIndex]), 0);

    final double newOrigin = dataset.getX(Math.max(0, pointIndex - 10));
    double newdomain =
        dataset.getX(Math.min(dataset.getNumSamples(), pointIndex + 10))
            - newOrigin;

    animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
  }

  private void setFocusAndNotifyView(Focus focus) {
    if (focus == null) {
      this.focus = null;
      view.fireFocusEvent(this, -1, -1);
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

  private void setupDatasetListeners() {
    for (int i = 0; i < datasets.length; i++) {
      XYDataset dataset = datasets[i];
      if (dataset instanceof UpdateableXYDataset) {
        ((UpdateableXYDataset) dataset).addXYDatasetListener(this);
      }
      if (dataset instanceof HasRegions) {
        ((HasRegions) dataset).addRegionLoadListener(this);
      }
    }
  }

  private double windowYtoUser(int y) {
    return (plotBounds.height - (y - plotBounds.y)) / plotBounds.height;
  }
}
