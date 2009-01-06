package org.timepedia.chronoscope.client.plot;

import com.google.gwt.gen2.event.shared.HandlerManager;
import com.google.gwt.gen2.event.shared.HandlerRegistration;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.HistoryManager;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.event.PlotContextMenuEvent;
import org.timepedia.chronoscope.client.event.PlotFocusEvent;
import org.timepedia.chronoscope.client.event.PlotFocusHandler;
import org.timepedia.chronoscope.client.event.PlotHoverEvent;
import org.timepedia.chronoscope.client.event.PlotHoverHandler;
import org.timepedia.chronoscope.client.event.PlotMovedEvent;
import org.timepedia.chronoscope.client.event.PlotMovedHandler;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.render.Background;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.DomainAxisPanel;
import org.timepedia.chronoscope.client.render.DrawableDataset;
import org.timepedia.chronoscope.client.render.GssBackground;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.render.ZoomListener;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class DefaultXYPlot<T extends Tuple2D>
    implements XYPlot<T>, Exportable, DatasetListener<T>, ZoomListener {

  // Indicator that nothing is selected (e.g. a data point or a data set).
  public static final int NO_SELECTION = -1;

  private static int globalPlotNumber = 0;

  private static final String LAYER_HOVER = "hoverLayer";

  private static final String LAYER_PLOT = "plotLayer";

  // The maximum distance that the mouse pointer can stray from a candidate
  // data point and still be considered as referring to that point.
  private static final int MAX_FOCUS_DIST = 8;

  // The maximum distance (only considers x-axis) that the mouse pointer can 
  // stray from a data point and still cause that point to be "hovered".
  private static final int MAX_HOVER_DIST = 8;

  private static final double MIN_PLOT_HEIGHT = 50;

  private static final double ZOOM_FACTOR = 1.50d;

  private static boolean pointExists(int pointIndex) {
    return pointIndex > NO_SELECTION;
  }

  private PortableTimerTask animationContinuation;

  private PortableTimer animationTimer;

  private Background background;

  private double beginHighlight = Double.MIN_VALUE, endHighlight
      = Double.MIN_VALUE;

  private Datasets<T> datasets;
  
  private boolean highlightDrawn;

  private Focus focus = null;

  private BottomPanel bottomPanel;
  
  private TopPanel topPanel;
  
  private RangePanel rangePanel;
  
  private Layer highLightLayer, plotLayer, hoverLayer;

  private int[] hoverPoints;

  private Bounds innerBounds;

  private boolean isAnimating = false;

  private int maxDrawableDatapoints = 400;

  private final NearestPoint nearestSingleton = new NearestPoint();

  private ArrayList<Overlay> overlays;

  private Bounds plotBounds;

  public Interval visDomain, lastVisDomain, widestDomain;
  
  int plotNumber = 0;

  private XYPlotRenderer<T> plotRenderer;

  private View view;

  private double visibleDomainMax;

  private HandlerManager handlerManager = new HandlerManager(this);

  public DefaultXYPlot() {
    overlays = new ArrayList<Overlay>();
    plotNumber = globalPlotNumber++;
    
    bottomPanel = new BottomPanel();
    topPanel = new TopPanel();
    rangePanel = new RangePanel();
  }

  /**
   * @gwt.export
   */
  @Export
  public void addOverlay(Overlay overlay) {
    overlays.add(overlay);
    overlay.setPlot(this);
    this.drawOverlays(plotLayer);
  }
  
  public HandlerRegistration addPlotFocusHandler(PlotFocusHandler handler) {
    return handlerManager.addHandler(PlotFocusEvent.TYPE, handler);
  }

  public HandlerRegistration addPlotHoverHandler(PlotHoverHandler handler) {
    return handlerManager.addHandler(PlotHoverEvent.TYPE, handler);
  }
  
  public HandlerRegistration addPlotMovedHandler(PlotMovedHandler handler) {
    return handlerManager.addHandler(PlotMovedEvent.TYPE, handler);
  }

  public void animateTo(final double destDomainOrigin,
      final double destCurrentDomain, final PlotMovedEvent.MoveType eventType) {

    animateTo(destDomainOrigin, destCurrentDomain, eventType, null);
  }

  public void animateTo(final double destDomainOrigin,
      final double destCurrentDomain, final PlotMovedEvent.MoveType eventType,
      final PortableTimerTask continuation) {

    animateTo(destDomainOrigin, destCurrentDomain, eventType, continuation,
        true);
  }

  public boolean click(int x, int y) {
    if (setFocusXY(x, y)) {
      return true;
    }

    for (Overlay o : overlays) {
      boolean wasOverlayHit = 
        visDomain.contains(o.getDomainX()) && o.isHit(x, y);

      if (wasOverlayHit) {
        o.click(x, y);
        return true;
      }
    }

    return topPanel.isEnabled() ? topPanel.click(x, y) : false;
  }

  /**
   * Any cached drawings of this axis are flushed and redrawn on next update
   */
  public void damageAxes(ValueAxis axis) {
    rangePanel.clearDrawCaches();
  }

  public double domainToScreenX(double dataX, int datasetIndex) {
    ValueAxis valueAxis = bottomPanel.getDomainAxisPanel().getValueAxis();
    double userX = valueAxis.dataToUser(dataX);
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
    if (!visDomain.containsOpen(domainX)) {
      scrollAndCenter(domainX, callback);
      return true;
    }
    return false;
  }

  public void fireContextMenuEvent(int x, int y) {
    handlerManager.fireEvent(new PlotContextMenuEvent(this, x, y));
  }

  public Bounds getBounds() {
    return plotBounds;
  }

  public Chart getChart() {
    return view.getChart();
  }

  public int getCurrentMipLevel(int datasetIndex) {
    return plotRenderer.getDrawableDataset(datasetIndex).currMipMap.getLevel();
  }

  public DatasetRenderer<T> getDatasetRenderer(int datasetIndex) {
    return plotRenderer.getDrawableDataset(datasetIndex).getRenderer();
  }

  /**
   * Returns the datasets associated with this plot.
   */
  public Datasets<T> getDatasets() {
    return this.datasets;
  }

  public double getDataX(int datasetIndex, int pointIndex) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return dds.currMipMap.getDomain().get(pointIndex);
  }

  public double getDataY(int datasetIndex, int pointIndex) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return dds.currMipMap.getTuple(pointIndex).getRange0();
  }

  public Interval getDomain() {
    return this.visDomain;
  }

  public DomainAxisPanel getDomainAxisPanel() {
    return bottomPanel.getDomainAxisPanel();
  }

  public Focus getFocus() {
    return this.focus;
  }

  public String getHistoryToken() {
    return getChart().getChartId() + "(O" + visDomain.getStart() + ",D"
        + visDomain.length() + ")";
  }

  public Layer getHoverLayer() {
    return initLayer(null, LAYER_HOVER, plotBounds);
  }

  public int[] getHoverPoints() {
    return this.hoverPoints;
  }

  public Bounds getInnerBounds() {
    return innerBounds;
  }

  public int getMaxDrawableDataPoints() {
    return (int) (isAnimating ? maxDrawableDatapoints
        : ChronoscopeOptions.getMaxStaticDatapoints());
  }

  public int getNearestVisiblePoint(double domainX, int datasetIndex) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return Util.binarySearch(dds.currMipMap.getDomain(), domainX);
  }

  public OverviewAxisPanel getOverviewAxisPanel() {
    return bottomPanel.getOverviewAxisPanel();
  }

  public Layer getOverviewLayer() {
    return bottomPanel.getOveriewLayer();
  }

  public Layer getPlotLayer() {
    return initLayer(null, LAYER_PLOT, plotBounds);
  }

  /**
   * @gwt.export getAxis
   */
  @Export("getAxis")
  public RangeAxis getRangeAxis(int datasetIndex) {
    return rangePanel.getRangeAxes().get(datasetIndex);
  }

  public double getSelectionBegin() {
    return beginHighlight;
  }

  public double getSelectionEnd() {
    return endHighlight;
  }

  public double getVisibleDomainMax() {
    return visibleDomainMax;
  }

  public Interval getWidestDomain() {
    return this.widestDomain;
  }
  
  public void init(View view) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");
    ArgChecker.isNotNull(plotRenderer, "plotRenderer");

    this.view = view;
    this.focus = null;

    initViewIndependent(datasets);
    
    if (!plotRenderer.isInitialized()) {
      plotRenderer.setPlot(this);
      plotRenderer.setView(view);
      plotRenderer.init();
    }
    else {
      plotRenderer.resetMipMapLevels();
    }
    
    widestDomain = plotRenderer.calcWidestPlotDomain();
    visDomain = widestDomain.copy();
    
    ArgChecker.isNotNull(view.getCanvas(), "view.canvas");
    ArgChecker
        .isNotNull(view.getCanvas().getRootLayer(), "view.canvas.rootLayer");
    view.getCanvas().getRootLayer().setVisibility(true);
    
    if (bottomPanel.isInitialized()) {
      bottomPanel.layout();
    } else {
      initAuxiliaryPanel(bottomPanel, view);
    }
    
    if (rangePanel.isInitialized()) {
      // Need to initLayer() to ensure that all text layers get wiped out
      rangePanel.layout();
    } else {
      initAuxiliaryPanel(rangePanel, view);
    }
    
    // TODO: the top panel's initialization currently depends on the initialization
    // of the bottomPanel.  Remove this dependency if possible.
    if (topPanel.isInitialized()) {
      topPanel.layout();
    } else {
      initAuxiliaryPanel(topPanel, view);
    }
    
    plotBounds = computePlotBounds();
    innerBounds = new Bounds(0, 0, plotBounds.width, plotBounds.height);

    clearDrawCaches();
    lastVisDomain = new Interval(0, 0);
    
    initLayers();
    
    background = new GssBackground(view);  
    view.canvasSetupDone();
  }

  public boolean isAnimating() {
    return isAnimating;
  }

  public boolean isOverviewEnabled() {
    return bottomPanel.isOverviewEnabled();
  }

  public void maxZoomOut() {
    pushHistory();
    animateTo(widestDomain.getStart(), widestDomain.length(), 
        PlotMovedEvent.MoveType.ZOOMED);
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
    movePlotDomain(domainX);
    fireMoveEvent(PlotMovedEvent.MoveType.DRAGGED);
    this.redraw();
  }

  public void nextFocus() {
    shiftFocus(+1);
  }

  public void nextZoom() {
    pushHistory();
    double nDomain = visDomain.length() / ZOOM_FACTOR;
    animateTo(visDomain.midpoint() - nDomain / 2, nDomain,
        PlotMovedEvent.MoveType.ZOOMED);
  }

  public void onDatasetAdded(Dataset<T> dataset) {
    // Range panel needs to be set back to an uninitialized state (in 
    // particular so that it calls its autoAssignDatasetAxes() method).
    //
    // TODO: auxiliary panels should listen to dataset events directly
    // and respond accordingly, rather than forcing this class to manage
    // everything.
    //this.initAuxiliaryPanel(this.rangePanel, this.view);
    this.rangePanel = new RangePanel();
    this.plotRenderer.addDataset(this.datasets.size() - 1, dataset);
    this.initAndRedraw();
  }

  public void onDatasetChanged(Dataset<T> dataset, double domainStart,
      double domainEnd) {
    visibleDomainMax = calcVisibleDomainMax(getMaxDrawableDataPoints(), datasets);
    int datasetIndex = this.datasets.indexOf(dataset);
    if (datasetIndex == -1) {
      datasetIndex = 0;
    }
    damageAxes(getRangeAxis(datasetIndex));
    if (domainEnd > visDomain.getEnd()) {
      animateTo(domainEnd - visDomain.length() / 2, visDomain.length(),
          PlotMovedEvent.MoveType.DRAGGED, new PortableTimerTask() {
            public void run(PortableTimer timer) {
              redraw(true);
            }
          }, false);
    } else {
      redraw(true);
    }
  }

  public void onDatasetRemoved(Dataset<T> dataset, int datasetIndex) {
    if (datasets.isEmpty()) {
      throw new IllegalStateException(
          "Datasets container is empty -- can't render plot.");
    }

    // Remove any marker overlays bound to the removed dataset.
    List<Overlay> tmpOverlays = overlays;
    overlays = new ArrayList<Overlay>();
    for (int i = 0; i < tmpOverlays.size(); i++) {
      Overlay o = tmpOverlays.get(i);
      if (o instanceof Marker) {
        Marker m = (Marker) o;
        int markerDatasetIdx = m.getDatasetIndex();
        boolean doRemoveMarker = false;
        if (markerDatasetIdx == datasetIndex) {
          m.setDatasetIndex(-1);
          doRemoveMarker = true;
        } else if (markerDatasetIdx > datasetIndex) {
          // HACKITY-HACK! 
          // Since Marker objects currently store the
          // ordinal position of the dataset to which they are bound,
          // we need to decrement all of the indices that are >= 
          // the index of the dataset being removed.
          m.setDatasetIndex(markerDatasetIdx - 1);
        }

        if (!doRemoveMarker) {
          overlays.add(o);
        }
      } else {
        overlays.add(o);
      }
    }
    
    this.rangePanel.initLayer();
    this.rangePanel = new RangePanel();
    this.plotRenderer.removeDataset(dataset);
    initAndRedraw();
  }

  public void onZoom(double intervalInMillis) {
    if (intervalInMillis == Double.MAX_VALUE) {
      maxZoomOut();
    } else {
      double domainStart = getDomain().midpoint() - (intervalInMillis / 2);
      animateTo(domainStart, intervalInMillis, PlotMovedEvent.MoveType.ZOOMED, null);
    }
  }

  public InfoWindow openInfoWindow(final String html, final double domainX,
      final double rangeY, final int datasetIndex) {

    final InfoWindow window = view
        .createInfoWindow(html, domainToWindowX(domainX, datasetIndex),
            rangeToWindowY(rangeY, datasetIndex) + 5);

    final PortableTimerTask timerTask = new PortableTimerTask() {
        public void run(PortableTimer timer) {
          window.open();
        }
    };
    
    if (!ensureVisible(domainX, rangeY, timerTask)) {
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
    double nDomain = visDomain.length() * ZOOM_FACTOR;
    animateTo(visDomain.midpoint() - nDomain / 2, nDomain,
        PlotMovedEvent.MoveType.ZOOMED);
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
    redraw(false);
  }

  /**
   * If <tt>forceCenterPlotRedraw==false</tt>, the center plot (specifically the
   * datasets and overlays) is only redrawn when the state of
   * <tt>this.plotDomain</tt> changes. Otherwise if <tt>forceDatasetRedraw==true</tt>,
   * the center plot is redrawn unconditionally.
   */
  public void redraw(boolean forceCenterPlotRedraw) {
    Canvas backingCanvas = view.getCanvas();
    backingCanvas.beginFrame();

    // if on a low performance device, don't re-render axes or legend
    // when animating
    final boolean canDrawFast = !(isAnimating() && ChronoscopeOptions
        .isLowPerformance());

    final boolean plotDomainChanged = !visDomain.equals(lastVisDomain);

    // Draw the hover points, but not when the plot is currently animating.
    if (isAnimating) {
      getHoverLayer().clear();
    } else {
      plotRenderer.drawHoverPoints();
    }
    
    if (plotDomainChanged || forceCenterPlotRedraw) {
      plotLayer.save();
      plotLayer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
      plotLayer.clear();
      drawPlot();
      plotLayer.restore();

      rangePanel.draw();
      
      if (canDrawFast) {
        bottomPanel.draw();
        drawOverlays(plotLayer);
      }
    }

    if (canDrawFast) {
      topPanel.draw();
      drawPlotHighlight(highLightLayer);
    }
    
    backingCanvas.endFrame();
    visDomain.copyTo(lastVisDomain);
    view.flipCanvas();
  }

  /**
   * @gwt.export
   */
  @Export
  public void reloadStyles() {
    bottomPanel.clearDrawCaches();
    Interval tmpPlotDomain = visDomain.copy();
    init(view);
    ArrayList<Overlay> oldOverlays = overlays;
    overlays = new ArrayList<Overlay>();
    visDomain = plotRenderer.calcWidestPlotDomain();
    redraw();
    tmpPlotDomain.copyTo(visDomain);
    overlays = oldOverlays;
    redraw(true);
  }

  public void removeOverlay(Overlay over) {
    overlays.remove(over);
  }

  public void scrollAndCenter(double domainX, PortableTimerTask continuation) {
    pushHistory();

    final double newOrigin = domainX - visDomain.length() / 2;
    animateTo(newOrigin, visDomain.length(), PlotMovedEvent.MoveType.CENTERED,
        continuation);
  }

  public void scrollPixels(int amt) {
    final double domainAmt = (double) amt / plotBounds.width * visDomain
        .length();
    final double minDomain = widestDomain.getStart();
    final double maxDomain = widestDomain.getEnd();

    double newDomainOrigin = visDomain.getStart() + domainAmt;
    if (newDomainOrigin + visDomain.length() > maxDomain) {
      newDomainOrigin = maxDomain - visDomain.length();
    } else if (newDomainOrigin < minDomain) {
      newDomainOrigin = minDomain;
    }
    movePlotDomain(newDomainOrigin);
    fireMoveEvent(PlotMovedEvent.MoveType.DRAGGED);
    redraw();
  }

  public void setAnimating(boolean animating) {
    this.isAnimating = animating;
  }

  public void setAutoZoomVisibleRange(int dataset, boolean autoZoom) {
    rangePanel.getRangeAxes().get(dataset).setAutoZoomVisibleRange(autoZoom);
  }

  public void setDatasetRenderer(int datasetIndex, DatasetRenderer<T> renderer) {
    ArgChecker.isNotNull(renderer, "renderer");
    this.plotRenderer.setDatasetRenderer(datasetIndex, renderer);
    this.initAndRedraw();
  }

  public void setDatasets(Datasets<T> datasets) {
    ArgChecker.isNotNull(datasets, "datasets");
    ArgChecker.isGT(datasets.size(), 0, "datasets.size");
    datasets.addListener(this);
    this.datasets = datasets;
  }

  public void setDomainAxisPanel(DomainAxisPanel domainAxisPanel) {
    bottomPanel.setDomainAxisPanel(domainAxisPanel);
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

  public void setHighlight(double startDomainX, double endDomainX) {
    beginHighlight = startDomainX;
    endHighlight = endDomainX;
  }

  public void setHighlight(int selStart, int selEnd) {
    int tmp = Math.min(selStart, selEnd);
    selEnd = Math.max(selStart, selEnd);
    selStart = tmp;
    beginHighlight = windowXtoDomain(selStart);
    endHighlight = windowXtoDomain(selEnd);
    redraw();
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
      fireHoverEvent();
      redraw();
    }

    return isCloseToCurve;
  }

  @Export
  public void setLegendEnabled(boolean b) {
    topPanel.setEnabled(b);
  }

  @Export
  public void setOverviewEnabled(boolean overviewEnabled) {
    bottomPanel.setOverviewEnabled(overviewEnabled);
  }
  
  @Export
  public void setSubPanelsEnabled(boolean enabled) {
    topPanel.setEnabled(enabled);
    bottomPanel.setEnabled(enabled);
    rangePanel.setEnabled(enabled);
  }

  public void setPlotRenderer(XYPlotRenderer<T> plotRenderer) {
    if (plotRenderer != null) {
      plotRenderer.setPlot(this);
    }
    this.plotRenderer = plotRenderer;
  }
  
  public double windowXtoUser(double x) {
    return (x - plotBounds.x) / plotBounds.width;
  }

  public void zoomToHighlight() {
    final double newOrigin = beginHighlight;
    double newdomain = endHighlight - beginHighlight;
    pushHistory();
    animateTo(newOrigin, newdomain, PlotMovedEvent.MoveType.ZOOMED);
  }

  private void animateTo(final double destDomainOrigin,
      final double destDomainLength, final PlotMovedEvent.MoveType eventType,
      final PortableTimerTask continuation, final boolean fence) {

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
    } else {
      destDomain = new Interval(destDomainOrigin,
          destDomainOrigin + destDomainLength);
    }

    animationContinuation = continuation;
    final Interval visibleDomain = this.visDomain;
    
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

        final double domainCenter =
            (destDomainMid - srcDomain.midpoint()) * lerpFactor + srcDomain
                .midpoint();
        final double domainLength = srcDomain.length() * ((1 - lerpFactor) + (
            zoomFactor * lerpFactor));
        final double domainStart = domainCenter - domainLength / 2;
        visibleDomain.setEndpoints(domainStart, domainStart + domainLength);
        redraw();

        if (lerpFactor < 1) {
          t.schedule(10);
        } else if (lastFrame) {
          fireMoveEvent(eventType);
          if (continuation != null) {
            continuation.run(t);
            animationContinuation = null;
          }
          isAnimating = false;
          animationTimer = null;
          redraw(true);
        } else {
          lastFrame = true;
          animationTimer.schedule(300);
        }
      }
    });

    animationTimer.schedule(10);
  }

  private void clearDrawCaches() {
    bottomPanel.clearDrawCaches();
    topPanel.clearDrawCaches();
    rangePanel.clearDrawCaches();
  }

  private Bounds computePlotBounds() {
    final double viewWidth = view.getWidth();
    final double viewHeight = view.getHeight();
    Bounds b = new Bounds();

    double centerPlotHeight = viewHeight - topPanel.getHeight() - 
        bottomPanel.getHeight();

    // If center plot too squished, remove the overview axis
    if (centerPlotHeight < MIN_PLOT_HEIGHT) {
      if (bottomPanel.isOverviewEnabled()) {
        bottomPanel.setOverviewEnabled(false);
        //bottomPanel.removeOverviewAxisPanel();
        centerPlotHeight = viewHeight - topPanel.getHeight() - 
            bottomPanel.getHeight();
      }
    }

    // If center plot still too squished, remove the legend axis
    if (centerPlotHeight < MIN_PLOT_HEIGHT) {
      if (topPanel.isEnabled()) {
        topPanel.setEnabled(false);
        centerPlotHeight = viewHeight - topPanel.getHeight() - 
            bottomPanel.getHeight();
      }
    }

    b.x = rangePanel.getLeftSubPanel().getWidth();
    b.y = topPanel.getHeight();
    b.height = centerPlotHeight;
    b.width = viewWidth - rangePanel.getLeftSubPanel().getWidth() - 
        rangePanel.getRightSubPanel().getWidth();

    return b;
  }

  /**
   * Draws the overlays (e.g. markers) onto the center plot.
   */
  private void drawOverlays(Layer layer) {
    layer.save();
    layer.clearTextLayer("overlays");
    layer.setTextLayerBounds("overlays",
        new Bounds(0, 0, layer.getBounds().width, layer.getBounds().height));

    for (Overlay o : overlays) {
      o.draw(layer, "overlays");
    }

    layer.restore();
  }

  void drawPlot() {
    plotLayer.setScrollLeft(0);
    background
        .paint(this, plotLayer, visDomain.getStart(), visDomain.length());
    // reset the visible RangeAxis ticks if it's been zoomed
    for (RangeAxis axis : rangePanel.getRangeAxes()) {
      axis.initVisibleRange();
    }
    plotRenderer.drawDatasets();
  }

  /**
   * Draws the highlighted region onto the center plot.
   */
  private void drawPlotHighlight(Layer layer) {
    final double domainStart = visDomain.getStart();
    final double domainEnd = visDomain.getEnd();

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
    layer.setFillColor(new Color("#14FFFF"));
    // layer.setLayerAlpha(0.2f);
    layer.setTransparency(0.2f);
    layer.clear();
    layer.fillRect(ux, 0, ex - ux, getInnerBounds().height);
    layer.restore();
    highlightDrawn = true;
  }

  private Interval fenceDomain(double destDomainOrig, double destDomainLength) {
    final double minDomain = widestDomain.getStart();
    final double maxDomain = widestDomain.getEnd();
    final double maxDomainLength = maxDomain - minDomain;
    final double minTickSize = 
        bottomPanel.getDomainAxisPanel().getMinimumTickSize();

    // First ensure that the destination domain length is smaller than the 
    // difference between the minimum and maximum dataset domain values.  
    // Then ensure that the destDomain is larger than what the DateAxis thinks
    //is it's smallest tick interval it can handle.
    final double fencedDomainLength = Math
        .max(Math.min(destDomainLength, maxDomainLength), minTickSize);

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
    return new Interval(fencedDomainOrigin,
        fencedDomainOrigin + fencedDomainLength);
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

    MipMap currMipMap = plotRenderer.getDrawableDataset(datasetIndex).currMipMap;
    
    // Find index of data point closest to the right of dataX at the current MIP level
    int closestPtToRight = Util.binarySearch(currMipMap.getDomain(), dataX);

    double sx = domainToScreenX(dataX, datasetIndex);
    double sy = rangeToScreenY(dataY, datasetIndex);
    Tuple2D tupleRight = currMipMap.getTuple(closestPtToRight);
    double rx = domainToScreenX(tupleRight.getDomain(), datasetIndex);
    double ry = rangeToScreenY(tupleRight.getRange0(), datasetIndex);

    int nearestHoverPt;
    if (closestPtToRight == 0) {
      nearestHoverPt = closestPtToRight;
      np.dist = df.dist(sx, sy, rx, ry);
    } else {
      int closestPtToLeft = closestPtToRight - 1;
      Tuple2D tupleLeft = currMipMap.getTuple(closestPtToLeft);
      double lx = domainToScreenX(tupleLeft.getDomain(), datasetIndex);
      double ly = rangeToScreenY(tupleLeft.getRange0(), datasetIndex);
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

  private void fireFocusEvent(int datasetIndex, int pointIndex) {
    handlerManager
        .fireEvent(new PlotFocusEvent(this, pointIndex, datasetIndex));
  }

  private void fireHoverEvent() {
    handlerManager
        .fireEvent(new PlotHoverEvent(this, Util.copyArray(hoverPoints)));
  }

  private void fireMoveEvent(PlotMovedEvent.MoveType moveType) {
    handlerManager
        .fireEvent(new PlotMovedEvent(this, getDomain().copy(), moveType));
  }

  private void initAndRedraw() {
    init(this.view);
    redraw(true);
  }

  Layer initLayer(Layer layer, String layerPrefix, Bounds layerBounds) {
    Canvas canvas = view.getCanvas();
    if (layer != null) {
      canvas.disposeLayer(layer);
    }
    return canvas.createLayer(layerPrefix + plotNumber, layerBounds);
  }

  /**
   * Initializes the layers needed by the center plot.
   */
  private void initLayers() {
    view.getCanvas().getRootLayer().setLayerOrder(Layer.Z_LAYER_BACKGROUND);

    plotLayer = initLayer(plotLayer, LAYER_PLOT, plotBounds);

    highLightLayer = initLayer(highLightLayer, "highlight", plotBounds);
    highLightLayer.setLayerOrder(Layer.Z_LAYER_HIGHLIGHT);

    hoverLayer = initLayer(hoverLayer, LAYER_HOVER, plotBounds);
    hoverLayer.setLayerOrder(Layer.Z_LAYER_HOVER);
  
    topPanel.initLayer();
    bottomPanel.initLayer();
    rangePanel.initLayer();
  }

  /**
   * Methods which do not depend on any visual state of the chart being
   * initialized first. Can be moved early in Plot initialization. Put stuff
   * here that doesn't depend on the axes or layers being initialized.
   */
  private void initViewIndependent(Datasets<T> datasets) {
    maxDrawableDatapoints = ChronoscopeOptions.getMaxDynamicDatapoints()
        / datasets.size();
    visibleDomainMax = calcVisibleDomainMax(getMaxDrawableDataPoints(), datasets);
    resetHoverPoints(datasets.size());
  }

  /**
   * Returns true only if this plot is in a state such that animations (e.g.
   * zoom in, pan) are possible.
   */
  private boolean isAnimatable() {
    return this.visDomain.length() != 0.0;
  }

  private void maxZoomToPoint(int pointIndex, int datasetIndex) {
    pushHistory();

    Dataset<T> dataset = datasets.get(datasetIndex);
    DrawableDataset dds = plotRenderer.getDrawableDataset(datasetIndex);
    double currMipLevelDomainX = dds.currMipMap.getDomain().get(pointIndex);
    Array1D rawDomain = dds.dataset.getMipMapChain().getMipMap(0).getDomain();
    pointIndex = Util.binarySearch(rawDomain, currMipLevelDomainX);

    final int zoomOffset = 10;
    final double newOrigin = dataset.getX(Math.max(0, pointIndex - zoomOffset));
    final double newdomain =
        dataset.getX(Math.min(dataset.getNumSamples(), pointIndex + zoomOffset))
            - newOrigin;

    animateTo(newOrigin, newdomain, PlotMovedEvent.MoveType.ZOOMED);
  }

  /**
   * Assigns a new domain start value while maintaining the current domain
   * length (i.e. the domain end value is implicitly modified).
   */
  private void movePlotDomain(double newDomainStart) {
    double len = this.visDomain.length();
    this.visDomain.setEndpoints(newDomainStart, newDomainStart + len);
  }

  private void page(double pageSize) {
    pushHistory();
    final double newOrigin = visDomain.getStart() + (visDomain.length()
        * pageSize);
    animateTo(newOrigin, visDomain.length(), PlotMovedEvent.MoveType.PAGED);
  }

  private void pushHistory() {
    HistoryManager.pushHistory();
  }

  /**
   * Fills this.hoverPoints[] with {@link #NO_SELECTION} values.
   * If hoverPoints[] is null or not the same length as this.datsets.size(), 
   * it is initialized to the correct size.
   */
  private void resetHoverPoints(int numDatasets) {
    if (hoverPoints == null || hoverPoints.length != numDatasets) {
      hoverPoints = new int[numDatasets];
    }
    Arrays.fill(hoverPoints, NO_SELECTION);
  }

  private void setFocusAndNotifyView(Focus focus) {
    if (focus == null) {
      this.focus = null;
      fireFocusEvent(NO_SELECTION, NO_SELECTION);
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

    fireFocusEvent(datasetIndex, pointIndex);
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

    Dataset<T> ds;
    int focusDatasetIdx, focusPointIdx;
    
    if (focus == null) {
      // If no data point currently has the focus, then set the focus point to
      // the point on dataset [0] that's closest to the center of the screen.
      focusDatasetIdx = 0;
      ds = datasets.get(focusDatasetIdx);
      MipMap mipMap = plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap;
      double domainCenter = visDomain.midpoint();
      focusPointIdx = Util.binarySearch(mipMap.getDomain(), domainCenter);
    } else {
      // some data point currently has the focus.
      focusDatasetIdx = focus.getDatasetIndex();
      focusPointIdx = focus.getPointIndex();
      MipMap mipMap = plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap;
      focusPointIdx += n;

      if (focusPointIdx >= mipMap.size()) {
        ++focusDatasetIdx;
        if (focusDatasetIdx >= datasets.size()) {
          focusDatasetIdx = 0;
        }
        focusPointIdx = 0;
      } else if (focusPointIdx < 0) {
        --focusDatasetIdx;
        if (focusDatasetIdx < 0) {
          focusDatasetIdx = datasets.size() - 1;
        }
        focusPointIdx = plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap.size() - 1;
      }

      ds = datasets.get(focusDatasetIdx);
    }
    
    MipMap currMipMap = plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap;
    Tuple2D dataPt = currMipMap.getTuple(focusPointIdx);
    double dataX = dataPt.getDomain();
    double dataY = dataPt.getRange0();
    ensureVisible(dataX, dataY, null);
    setFocusAndNotifyView(focusDatasetIdx, focusPointIdx);
    redraw();
  }

  private double windowXtoDomain(double x) {
    return bottomPanel.getDomainAxisPanel()
        .getValueAxis().userToData(windowXtoUser(x));
  }

  private double windowYtoRange(int y, int datasetIndex) {
    double userY = (plotBounds.height - (y - plotBounds.y)) / plotBounds.height;
    return getRangeAxis(datasetIndex).userToData(userY);
  }
  
  private void initAuxiliaryPanel(AuxiliaryPanel panel, View view) {
    panel.setPlot(this);
    panel.setView(view);
    panel.init();
  }
  
  
  /**
   * Returns the greatest domain value across all datasets for the specified
   * <tt>maxDrawableDataPoints</tt> value.  For each dataset, the max domain value 
   * is obtained from the lowest mip level (i.e. highest resolution) whose 
   * corresponding datapoint cardinality is not greater than
   * <tt>maxDrawableDataPoints</tt>.
   */
  private static <T extends Tuple2D> double calcVisibleDomainMax(int maxDrawableDataPoints,
      Datasets<T> dataSets) {
    
    double end = Double.MIN_VALUE;
    
    for (Dataset<T> ds : dataSets) {
      // find the lowest mip level whose # of data points is not greater
      // than maxDrawableDataPoints
      MipMap mipMap = ds.getMipMapChain().findHighestResolution(maxDrawableDataPoints);
      end = Math.max(end, mipMap.getDomain().getLast());
    }
    return end;
  }

}
