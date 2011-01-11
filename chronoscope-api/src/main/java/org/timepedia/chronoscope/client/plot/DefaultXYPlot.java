package org.timepedia.chronoscope.client.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
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
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.RenderedPoint;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.event.ChartClickEvent;
import org.timepedia.chronoscope.client.event.ChartClickHandler;
import org.timepedia.chronoscope.client.event.PlotChangedEvent;
import org.timepedia.chronoscope.client.event.PlotChangedHandler;
import org.timepedia.chronoscope.client.event.PlotContextMenuEvent;
import org.timepedia.chronoscope.client.event.PlotFocusEvent;
import org.timepedia.chronoscope.client.event.PlotFocusHandler;
import org.timepedia.chronoscope.client.event.PlotHoverEvent;
import org.timepedia.chronoscope.client.event.PlotHoverHandler;
import org.timepedia.chronoscope.client.event.PlotMovedEvent;
import org.timepedia.chronoscope.client.event.PlotMovedHandler;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.render.Background;
import org.timepedia.chronoscope.client.render.DatasetLegendPanel;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.DomainAxisPanel;
import org.timepedia.chronoscope.client.render.DrawableDataset;
import org.timepedia.chronoscope.client.render.GssBackground;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.render.RenderState;
import org.timepedia.chronoscope.client.render.StringSizer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.render.ZoomListener;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.chronoscope.client.util.date.FastChronoDate;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

/**
 * A DefaultXYPlot is responsible for drawing the main chart area (excluding
 * axes), mapping one or more datasets from (domain,range) space to (x,y) screen
 * space by delegating to one or more ValueAxis implementations. Drawing for
 * each dataset is delegated to Renderers. A plot also maintains state like the
 * current selection and focus point.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
@ExportPackage("chronoscope")
public class DefaultXYPlot<T extends Tuple2D>
    implements XYPlot<T>, Exportable, DatasetListener<T>, ZoomListener {

  protected static double MIN_WIDTH_FACTOR = 1.5;
  protected static double MAX_WIDTH_FACTOR = 1.1;

  private boolean legendOverridden;
  
  private double lastZoomDomain = 0;

  private class ExportableHandlerManager extends HandlerManager {

    public ExportableHandlerManager(DefaultXYPlot<T> xyPlot) {
      super(xyPlot);
    }

    public ExportableHandlerRegistration addExportableHandler(
        GwtEvent.Type type, EventHandler handlerType) {
      super.addHandler(type, handlerType);
      return new ExportableHandlerRegistration(this, type, handlerType);
    }
  }

  // Indicator that nothing is selected (e.g. a data point or a data set).

  public static final int NO_SELECTION = -1;

  private static int globalPlotNumber = 0;

  private static final String LAYER_HOVER = "hoverLayer";
  private static final String LAYER_OVERLAY = "overlayLayer";
  private static final String LAYER_PLOT = "plotLayer";

  // The maximum distance that the mouse pointer can stray from a candidate
  // data point and still be considered as referring to that point.

  private static final int MAX_FOCUS_DIST = 8;

  // The maximum distance (only considers x-axis) that the mouse pointer can 
  // stray from a data point and still cause that point to be "hovered".

  private static final int MAX_HOVER_DIST = 8;

  private static final double MIN_PLOT_HEIGHT = 20;
  private static final double MIN_BOTTOM_HEIGHT = 16;

  private static final double ZOOM_FACTOR = 1.50d;

  /**
   * Returns the greatest domain value across all datasets for the specified
   * <tt>maxDrawableDataPoints</tt> value.  For each dataset, the max domain
   * value is obtained from the lowest mip level (i.e. highest resolution) whose
   * corresponding datapoint cardinality is not greater than
   * <tt>maxDrawableDataPoints</tt>.
   */
  private static <T extends Tuple2D> double calcVisibleDomainMax(
      int maxDrawableDataPoints, Datasets<T> dataSets) {

    double end = Double.MIN_VALUE;

    for (Dataset<T> ds : dataSets) {
      // find the lowest mip level whose # of data points is not greater
      // than maxDrawableDataPoints
      MipMap mipMap = ds.getMipMapChain()
          .findHighestResolution(maxDrawableDataPoints);
      end = Math.max(end, mipMap.getDomain().getLast());
    }
    return end;
  }

  private static boolean pointExists(int pointIndex) {
    return pointIndex > NO_SELECTION;
  }

  public Interval visDomain, lastVisDomain, widestDomain;

  protected View view;

  int plotNumber = 0;

  boolean firstDraw = true;

  private PortableTimer changeTimer = null;

  private int hoverX;

  private int hoverY;

  private boolean multiaxis = ChronoscopeOptions.getDefaultMultiaxisMode();

  private GssProperties crosshairProperties, crosshairLabelsProperties;

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

  private Layer plotLayer, hoverLayer, overlayLayer;

  private int[] hoverPoints;

  private Bounds innerBounds;

  private boolean isAnimating = false;

  private int maxDrawableDatapoints = 400;

  private final NearestPoint nearestSingleton = new NearestPoint();

  private ArrayList<Overlay> overlays;

  private Bounds plotBounds, hoverBounds;

  private XYPlotRenderer<T> plotRenderer;

  private StringSizer stringSizer;

  private double visibleDomainMax;

  private ExportableHandlerManager handlerManager = new ExportableHandlerManager(this);

  public DefaultXYPlot() {
    overlays = new ArrayList<Overlay>();
    plotNumber = globalPlotNumber++;

    bottomPanel = new BottomPanel();
    topPanel = new TopPanel();
    rangePanel = new RangePanel();
  }

  public <S extends GwtEvent.Type<T>, T extends EventHandler> HandlerRegistration addHandler(
      S type, T handler) {
    return handlerManager.addHandler(type, handler);
  }

  @Export
  public void addOverlay(Overlay overlay) {
    if (overlay != null) {
        overlays.add(overlay);
        overlay.setPlot(this);
    }
    //TODO: should we really redraw here? Kinda expensive if you want to bulk
    // add hundreds of overlays
    redraw(true);
  }

  @Export("addClickHandler")
  public ExportableHandlerRegistration addChartClickHandler(
      ChartClickHandler handler) {
    return handlerManager.addExportableHandler(ChartClickEvent.TYPE, handler);
  }

  @Export("addChangeHandler")
  public ExportableHandlerRegistration addPlotChangedHandler(
      PlotChangedHandler handler) {
    return handlerManager.addExportableHandler(PlotChangedEvent.TYPE, handler);
  }

  @Export("addFocusHandler")
  public ExportableHandlerRegistration addPlotFocusHandler(
      PlotFocusHandler handler) {
    return handlerManager.addExportableHandler(PlotFocusEvent.TYPE, handler);
  }

  @Export("addHoverHandler")
  public ExportableHandlerRegistration addPlotHoverHandler(
      PlotHoverHandler handler) {
    return handlerManager.addExportableHandler(PlotHoverEvent.TYPE, handler);
  }

  @Export("addMoveHandler")
  public ExportableHandlerRegistration addPlotMovedHandler(
      PlotMovedHandler handler) {
    return handlerManager.addExportableHandler(PlotMovedEvent.TYPE, handler);
  }

  
  @Export
  public void setTimeZoneOffsetUTC(int offsetHours) {
      if ((offsetHours > -14) && (offsetHours < 14)) {
        ChronoDate.setTimeZoneOffsetInMilliseconds(offsetHours*60*60*1000);
      }// else { // == 0
          // now 0 should really set to 0 offset from UTC, if you want local use OffsetBrowserLocal
          // ChronoDate.setTimeZoneOffsetInMilliseconds(ChronoDate.getLocalTimeZoneOffsetInMilliseconds());
      // }
      
      topPanel.getCompositePanel().draw();
      redraw(true);
      bottomPanel.draw();
  }

  @Export
  public void setTimeZoneOffsetBrowserLocal(int offsetHours) {
    // if ((offsetHours >= -12 && offsetHours < 0) || (offsetHours > 0 && offsetHours <= 13)) {
     if ((offsetHours > -14) && (offsetHours < 14)) {
        ChronoDate.setTimeZoneOffsetBrowserLocal(offsetHours*60*60*1000);
     }
     topPanel.getCompositePanel().draw();
     redraw(true);
     bottomPanel.draw();
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

  public double calcDisplayY(int datasetIdx, int pointIdx, int dimension) {
    DrawableDataset dds = plotRenderer.getDrawableDataset(datasetIdx);
    RangeAxis ra = getRangeAxis(datasetIdx);
    double y = dds.getRenderer()
        .getRangeValue(dds.currMipMap.getTuple(pointIdx), dimension);

    if (ra.isCalcRangeAsPercent()) {
      double refY = plotRenderer.calcReferenceY(ra, dds);
      y = RangeAxis.calcPrctDiff(refY, y);
    }

    return y;
  }

  public boolean click(int x, int y) {
    if (setFocusXY(x, y)) {
      return true;
    }

    for (Overlay o : overlays) {
      boolean wasOverlayHit = visDomain.contains(o.getDomainX()) && o.isHit(x, y);

      if (wasOverlayHit) {
        o.click(x, y);
        return true;
      }
    }

    if (topPanel.isEnabled()) {
      if (topPanel.click(x, y)) {
        return true;
      }
    }
    if (bottomPanel.isEnabled()) {
      if (bottomPanel.click(x, y)) {
        return true;
      }
    }
    handlerManager.fireEvent(new ChartClickEvent(this, x, y));
    return false;
  }

  @Export
  public void damageAxes() {
    rangePanel.clearDrawCaches();
    bottomPanel.clearDrawCaches();
    topPanel.clearDrawCaches();
  }

  public double domainToScreenX(double dataX, int datasetIndex) {
    ValueAxis valueAxis = bottomPanel.getDomainAxisPanel().getValueAxis();
    double userX = valueAxis.dataToUser(dataX);
    return userX * plotBounds.width;
  }

  public double domainToWindowX(double dataX, int datasetIndex) {
    return plotBounds.x + domainToScreenX(dataX, datasetIndex);
  }

  public void drawBackground() {
    background.paint(this, plotLayer, visDomain.getStart(), visDomain.length());
  }

  public void drawOverviewPlot(Layer overviewLayer) {
    // save original endpoints so they can be restored later
    Interval origVisPlotDomain = getDomain().copy();
    getWidestDomain().copyTo(getDomain());
    // Canvas backingCanvas = view.getCanvas();
    // backingCanvas.beginFrame();
    overviewLayer.save();
    overviewLayer.clear();
    overviewLayer.setVisibility(false);
    overviewLayer.setFillColor(Color.TRANSPARENT);
    overviewLayer.fillRect(0, 0, overviewLayer.getWidth(), overviewLayer.getHeight());

    Bounds oldBounds = plotBounds;
    Layer oldLayer = plotLayer;
    plotBounds = new Bounds(0, overviewLayer.getHeight() - OverviewAxisPanel.OVERVIEW_HEIGHT, overviewLayer.getWidth(), OverviewAxisPanel.OVERVIEW_HEIGHT);
    plotLayer = overviewLayer;
    plotRenderer.drawDatasets(true);
    plotBounds = oldBounds;
    plotLayer = oldLayer;
    overviewLayer.restore();
    // backingCanvas.endFrame();
    // restore original endpoints
    origVisPlotDomain.copyTo(getDomain());
  }

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

  public void fireEvent(GwtEvent event) {
    handlerManager.fireEvent(event);
  }

  public Bounds getBounds() {
    return plotBounds;
  }

  @Export
  public Chart getChart() {
    return view.getChart();
  }

  public int getCurrentMipLevel(int datasetIndex) {
    return plotRenderer.getDrawableDataset(datasetIndex).currMipMap.getLevel();
  }

  public double getDataCoord(int datasetIndex, int pointIndex, int dim) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return dds.currMipMap.getTuple(pointIndex).getRange(dim);
  }

  public Tuple2D getDataTuple(int datasetIndex, int pointIndex) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return dds.currMipMap.getTuple(pointIndex);
  }

  public DatasetRenderer<T> getDatasetRenderer(int datasetIndex) {
    return plotRenderer.getDrawableDataset(datasetIndex).getRenderer();
  }

  @Override
  @Export
  public GssProperties getComputedStyle(String gssSelector) {
    return view.getGssPropertiesBySelector(gssSelector);
  }

  /**
   * Returns the datasets associated with this plot.
   */
  @Export
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

  @Export
  public Interval getDomain() {
    return this.visDomain;
  }

  @Export
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

  public Layer getOverlayLayer() {
    return overlayLayer;
  }


  public Layer getHoverLayer() {
    return hoverLayer;
    // return initLayer(hoverLayer, LAYER_HOVER, plotBounds);
  }

  public int[] getHoverPoints() {
    return this.hoverPoints;
  }

  public Bounds getInnerBounds() {
    return innerBounds;
  }

  public int getMaxDrawableDataPoints() {
    return (int) (isAnimating && ChronoscopeOptions.isAnimationPreview() ? maxDrawableDatapoints
        : ChronoscopeOptions.getMaxStaticDatapoints());
  }

  public int getNearestVisiblePoint(double domainX, int datasetIndex) {
    DrawableDataset<T> dds = plotRenderer.getDrawableDataset(datasetIndex);
    return Util.binarySearch(dds.currMipMap.getDomain(), domainX);
  }

  public Overlay getOverlayAt(int x, int y) {
    if ((null != overlays) && overlays.size() > 0) {
        for (Overlay o : overlays) {
            boolean wasOverlayHit = visDomain.contains(o.getDomainX()) && o
                    .isHit(x, y);

            if (wasOverlayHit) {
                return o;
            }
        }
    }
    return null;
  }

  public OverviewAxisPanel getOverviewAxisPanel() {
    return bottomPanel.getOverviewAxisPanel();
  }

  public Layer getPlotLayer() {
    return plotLayer;
  }

  /**
   * Return the axis-y referenced by datasetIndex.
   * It could cause an outOfBoundException if the index is greater
   * to the number of axis-y in the plot.
   */
  @Export("getAxis")
  public RangeAxis getRangeAxis(int datasetIndex) {
    return rangePanel.getRangeAxes()[datasetIndex];
  }

  public int getRangeAxisCount() {
    return rangePanel.getRangeAxes().length;
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

  public boolean isAnimating() {
    return isAnimating;
  }

  public boolean isMultiaxis() {
    return multiaxis;
  }

  @Export
  public void maxZoomOut() {
    pushHistory();
    animateTo(widestDomain.getStart(), widestDomain.length(), PlotMovedEvent.MoveType.ZOOMED);
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

  @Export
  public void maxZoomToFocus() {
    if (focus != null) {
      maxZoomToPoint(focus.getPointIndex(), focus.getDatasetIndex());
    }
  }

  @Export
  public void moveTo(double domainX) {
    movePlotDomain(domainX);
    fireMoveEvent(PlotMovedEvent.MoveType.DRAGGED);
    this.redraw();
  }

  @Export
  public void nextFocus() {
    shiftFocus(+1);
  }
  
  public void nextZoom() {
    pushHistory();
    double nDomain = fixDomainWidth(visDomain.length() / ZOOM_FACTOR);
    if (lastZoomDomain != nDomain) {
      lastZoomDomain = nDomain;
      animateTo(visDomain.midpoint() - nDomain / 2, nDomain, PlotMovedEvent.MoveType.ZOOMED);
    }
  }
  
  public void onDatasetsReplaced(Datasets<T> datasets){
    this.plotRenderer.init();
  }

  public void onDatasetAdded(Dataset<T> dataset) {
    // Range panel needs to be set back to an uninitialized state (in 
    // particular so that it calls its autoAssignDatasetAxes() method).
    //
    // TODO: auxiliary panels should listen to dataset events directly
    // and respond accordingly, rather than forcing this class to manage
    // everything.
    //this.initAuxiliaryPanel(this.rangePanel, this.view);
    this.plotRenderer.addDataset(this.datasets.size()-1, dataset);
    //this.rangePanel = new RangePanel();
    fixDomainDisjoint();
    this.reloadStyles();
  }

  public void onDatasetChanged(final Dataset<T> dataset, final double domainStart, final double domainEnd) {
      view.createTimer(new PortableTimerTask() {
          @Override
          public void run(PortableTimer timer) {
              visibleDomainMax = calcVisibleDomainMax(getMaxDrawableDataPoints(),
                      datasets);
              int datasetIndex = DefaultXYPlot.this.datasets.indexOf(dataset);
              if (datasetIndex == -1) {
                  datasetIndex = 0;
              }
              plotRenderer.invalidate(dataset);
              fixDomainDisjoint();
              damageAxes();
              getRangeAxis(datasets.indexOf(dataset)).adjustAbsRange(dataset);
              redraw(true);
          }
      }).schedule(15);
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

    this.plotRenderer.removeDataset(dataset);
    fixDomainDisjoint();
    this.reloadStyles();
  }

  public void onZoom(double intervalInMillis) {
    if (intervalInMillis == Double.MAX_VALUE) {
      maxZoomOut();
    } else {
      double domainStart = getDomain().midpoint() - (intervalInMillis / 2);
      animateTo(domainStart, intervalInMillis, PlotMovedEvent.MoveType.ZOOMED,
          null);
    }
  }

  @Export
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

  @Export
  public void pageLeft(double pageSize) {
    page(-pageSize);
  }

  @Export
  public void pageRight(double pageSize) {
    page(pageSize);
  }

  @Export
  public void prevFocus() {
    shiftFocus(-1);
  }

  @Export
  public void prevZoom() {
    pushHistory();
    double nDomain = fixDomainWidth(visDomain.length() * ZOOM_FACTOR);
    if (lastZoomDomain != nDomain) {
      lastZoomDomain = nDomain;
      animateTo(visDomain.midpoint() - nDomain / 2, nDomain,
          PlotMovedEvent.MoveType.ZOOMED);
    }
  }
  // NOTE - really this is rangeToPlotY
  public double rangeToScreenY(Tuple2D pt, int datasetIndex, int dim) {
    DatasetRenderer dr = getDatasetRenderer(datasetIndex);
    return plotBounds.height
        - getRangeAxis(datasetIndex).dataToUser(dr.getRangeValue(pt, dim)) * plotBounds.height;
  }
  // NOTE - really this is rangeToPlotY
  public double rangeToScreenY(double dataY, int datasetIndex) {
    return plotBounds.height
        - getRangeAxis(datasetIndex).dataToUser(dataY) * plotBounds.height;
  }

  public double rangeToWindowY(double rangeY, int datasetIndex) {
    return plotBounds.y + rangeToScreenY(rangeY, datasetIndex);
  }

  
  @Export
  public void redraw() {
    redraw(firstDraw);
  }

  /**
   * If <tt>forceCenterPlotRedraw==false</tt>, the center plot (specifically the
   * datasets and overlays) is redrawn only when the state of <tt>this.plotDomain</tt>
   * changes. 
   * Otherwise if <tt>forceDatasetRedraw==true</tt>, the center plot is redrawn 
   * unconditionally.
   */
  public void redraw(boolean force) {
    redrawTimer.redraw(force);
  }

  /**
   * This is a hack:
   * This timer schedules a redraw for a few milliseconds, so as
   * new redraws comming in this interval are ignored.
   * This improves performance in IE.
   * 
   * TODO: study the code and avoid redraw(true) when possible.  
   */
  private RedrawTimer redrawTimer = new RedrawTimer();
  private class RedrawTimer extends Timer {
    boolean running = false;
    boolean force = false;
    int gracePeriode;
    Long last;
    
    private RedrawTimer() {
      gracePeriode = ChronoscopeOptions.isLowPerformance() ? 300 : 50;
    }
    
    public void run() {
      try {
        Long start = new Date().getTime();
        System.out.println("Executing realRedraw force=" + force);
        realRedraw(force);
        System.out.println("realRedraw force=" + force + " took: " + (new Date().getTime() - start) + "ms.");
        running = false;
        force = false;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    public void redraw(boolean f) {
      System.out.println("RedrawTimer, itercepted call to redraw(" + f + "), running:" + running);
      force = force || f;
      if (!running || ChronoscopeOptions.isLowPerformance()) {
        schedule(gracePeriode);
      }
    }
    
    public void schedule(int delayMillis) {
      int l = delayMillis;
      if (!running) {
        running = true;
        last = new Date().getTime();
      } else {
        cancel();
        l = Math.max(50, (int)(delayMillis - (new Date().getTime() - last)));
      }
      super.schedule(l);
    }
  }
  
  private void realRedraw(boolean forceCenterPlotRedraw) {
    view.getCanvas().beginFrame();
    plotLayer.save();
    // if on a low performance device, don't re-render axes or legend
    // when animating
    final boolean canDrawFast = !(isAnimating() &&
            ChronoscopeOptions.isAnimationPreview() && ChronoscopeOptions.isLowPerformance());

    final boolean plotDomainChanged = forceCenterPlotRedraw || !visDomain.approx(lastVisDomain);
            // || ChronoscopeOptions.isVerticalCrosshairEnabled();

    Layer hoverLayer = getHoverLayer();
    clearOverlayLayer(overlayLayer);

    // Draw the hover points, but not when the plot is currently animating.
    if (isAnimating || hoverX < 1) {
      // ...
    } else if (hoverLayer != null && !firstDraw ){
      hoverLayer.save();
      hoverLayer.clear();
      drawCrossHairs(hoverLayer);
      plotRenderer.drawHoverPoints(hoverLayer);
      hoverLayer.restore();
    }

    if (plotDomainChanged) {
      plotLayer.clear();
      drawBackground();
      plotLayer.save();
      plotLayer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
      drawPlot();
      plotLayer.restore();

      // rangePanel should be drawn after the plot 
      rangePanel.draw();
      
      if (canDrawFast) {
        bottomPanel.draw();
      }
    }
    
    // Overlays should be drawn always.
    drawOverlays(overlayLayer);

    if (canDrawFast) {
      topPanel.draw();
      drawPlotHighlight(hoverLayer);
    }
    plotLayer.restore();
    view.getCanvas().endFrame();
    visDomain.copyTo(lastVisDomain);
    view.flipCanvas();
    
    firstDraw = false;
  }

  @Export
  public void reloadStyles() {

    bottomPanel.clearDrawCaches();
    Interval tmpPlotDomain = visDomain.copy();

    // hack, eval order dependency
    initViewIndependent(datasets);
    fixDomainDisjoint();
    init(view, false);
    ArrayList<Overlay> oldOverlays = overlays;
    overlays = new ArrayList<Overlay>();
    visDomain = plotRenderer.calcWidestPlotDomain();
    tmpPlotDomain.copyTo(visDomain);
    overlays = oldOverlays;

    initCrosshairs();

    redraw(true);
  }


  @Export
  public void removeOverlay(Overlay over) {
    if (null == over) { return; }
    overlays.remove(over);
    over.setPlot(null);
  }

  public void scrollAndCenter(double domainX, PortableTimerTask continuation) {
    pushHistory();

    final double newOrigin = domainX - visDomain.length() / 2;
    animateTo(newOrigin, visDomain.length(), PlotMovedEvent.MoveType.CENTERED,
        continuation);
  }

  @Export
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
    if (changeTimer != null) {
      changeTimer.cancelTimer();
    }
    changeTimer = view.createTimer(new PortableTimerTask() {
      public void run(PortableTimer timer) {
        changeTimer = null;
        fireChangeEvent();
      }
    });
    changeTimer.schedule(1000);

    redraw(true);
  }

  public void setAnimating(boolean animating) {
    this.isAnimating = animating;
  }

  @Override
  @Export
  public void setAnimationPreview(boolean enabled) {
    ChronoscopeOptions.setAnimationPreview(enabled);
  }

  @Export
  public void setAutoZoomVisibleRange(int dataset, boolean autoZoom) {
    rangePanel.getRangeAxes()[dataset].setAutoZoomVisibleRange(autoZoom);
  }

  public void setDatasetRenderer(int datasetIndex, DatasetRenderer<T> renderer) {
    ArgChecker.isNotNull(renderer, "renderer");
    renderer.setCustomInstalled(true);
    this.plotRenderer.setDatasetRenderer(datasetIndex, renderer);
    this.reloadStyles();
  }

  @Export
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
    int nearestDim = 0;
    double minNearestDist = MAX_FOCUS_DIST;
    boolean somePointHasFocus = false;

    if((x>=plotBounds.x)&&(x<=plotBounds.x+plotBounds.width)&&(y>=plotBounds.y)&&(y<=plotBounds.y+plotBounds.height)) {
      int plotX = x - (int)plotBounds.x;
      int plotY = y - (int)plotBounds.y;
      if (plotX < 0 || plotY < 0) { return false; }
      for (int i = 0; i < datasets.size(); i++) {
        NearestPoint nearest = this.nearestSingleton;
        HashSet<RenderedPoint> nearby = getDatasetRenderer(i).getClickable(plotX, plotY);
        if ((null != nearby) && (nearby.size()>0)) {
          Iterator<RenderedPoint> clique = nearby.iterator();
          while(clique.hasNext()) {
            RenderedPoint pt = clique.next();
            // double domainX = pt.getDomain();
            // double rangeY = pt.getRange0();
            double sx = pt.getPlotX();
            double sy = pt.getPlotY();
            double distance = DistanceFormula.XY.dist(plotX, plotY, sx, sy);
            if (distance < minNearestDist) {
              nearestPt = pt.getDomainIndex();
              nearestSer = pt.getDatasetIndex();
              minNearestDist = distance;
              nearestDim = pt.getDimension();
            }
          }
        }
      }

      somePointHasFocus = pointExists(nearestPt);
      if (somePointHasFocus) {
        setFocusAndNotifyView(nearestSer, nearestPt, nearestDim);
        redraw(true);
      } else {
        setFocusAndNotifyView(null);
      }
    }
    return somePointHasFocus;
  }

  @Export
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
    this.hoverX = x - (int) plotBounds.x;
    this.hoverY = y - (int) plotBounds.y;

    boolean isCloseToCurve = false;
    final int closenessThreshold = MAX_FOCUS_DIST;

    // True iff one or more hoverPoints have changed since the last call to this method
    boolean isDirty = false;
    if (this.hoverX < 0 || this.hoverY < 0) { return false; }
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

      hoverPoints[i] = pointExists(nearestPointIdx) ? nearestPointIdx : NO_SELECTION;

      if (nearestHoverPt.dist <= closenessThreshold) {
        isCloseToCurve = true;
      }
    }

    if (isDirty) {
      fireHoverEvent();
    }
    redraw();

    return isCloseToCurve;
  }

  @Export
  public void setLegendEnabled(boolean b) {
    legendOverridden = true;
    for(int i = 0; i<hoverPoints.length; i++) {
      hoverPoints[i] = -1;
    }
    for(Dataset d : datasets) {
      if(plotRenderer.isInitialized()) {
        plotRenderer.invalidate(d);
      }
    }
    plotRenderer.resetMipMapLevels();
    plotRenderer.sync();
    
    topPanel.setEnabled(b);
    if (plotRenderer.isInitialized()) {
      reloadStyles();
    }
    
  }

  @Override
  @Export
  public void setLegendLabelsVisible(boolean visible) {
    GssProperties gss = getComputedStyle("axislegend labels");
    if (gss != null) {
      gss.setVisible(visible);
      setLegendEnabled(true);
    }
  }

  @Export
  public void setMultiaxis(boolean enabled) {
    this.multiaxis = enabled;
    reloadStyles();
  }

  @Export
  public boolean isOverviewVisible() {
    return bottomPanel.isOverviewVisible();
  }

  @Export
  public void setOverviewVisible(boolean overviewVisible) {
    bottomPanel.setOverviewVisible(overviewVisible);
  }

  // Deprecated, use setOverviewVisible instead.
  @Export @Deprecated
  public void setOverviewEnabled(boolean overviewEnabled) {
    // bottomPanel.setOverviewEnabled(overviewEnabled);
    setOverviewVisible(overviewEnabled);
  }

  @Export @Deprecated
  public boolean isOverviewEnabled() {
    return isOverviewVisible();
  }

  public void setPlotRenderer(XYPlotRenderer<T> plotRenderer) {
    if (plotRenderer != null) {
      plotRenderer.setPlot(this);
    }
    this.plotRenderer = plotRenderer;
  }

  @Export
  public void setSubPanelsEnabled(boolean enabled) {
    if (!plotRenderer.isInitialized()) {
        plotRenderer.resetMipMapLevels();
	plotRenderer.sync();
    }
    topPanel.setEnabled(enabled);
    bottomPanel.setEnabled(enabled);
    rangePanel.setEnabled(enabled);
  }

  @Export
  public void setVisibleRangeMax(int dataset, double visRangeMax) {
    rangePanel.getRangeAxes()[dataset].setVisibleRangeMax(visRangeMax);
  }

  @Export
  public void setVisibleRangeMin(int dataset, double visRangeMin) {
    rangePanel.getRangeAxes()[dataset].setVisibleRangeMin(visRangeMin);
  }

  public double windowXtoDomain(double x) {
    return bottomPanel.getDomainAxisPanel().getValueAxis().userToData(windowXtoUser(x));
  }


  public double windowXtoUser(double x) {
    return (x - plotBounds.x) / plotBounds.width;
  }

  private double windowYtoRange(int y, int datasetIndex) {
    double userY = (plotBounds.height - (y - plotBounds.y)) / plotBounds.height;
    return getRangeAxis(datasetIndex).userToData(userY);
  }

  @Export
  public void zoomToHighlight() {
    final double newOrigin = beginHighlight;
    double newdomain = endHighlight - beginHighlight;
    pushHistory();
    animateTo(newOrigin, newdomain, PlotMovedEvent.MoveType.ZOOMED);
  }

  /**
   * Methods which do not depend on any visual state of the chart being
   * initialized first. Can be moved early in Plot initialization. Put stuff
   * here that doesn't depend on the axes or layers being initialized.
   */
  protected void initViewIndependent(Datasets<T> datasets) {
    maxDrawableDatapoints = ChronoscopeOptions.getMaxDynamicDatapoints()
        / datasets.size();
    visibleDomainMax = calcVisibleDomainMax(getMaxDrawableDataPoints(),
        datasets);
    resetHoverPoints(datasets.size());
  }

  void drawPlot() {
    plotLayer.clearTextLayer("plotTextLayer");
    plotLayer.setScrollLeft(0);
    plotRenderer.drawDatasets();
  }

  Layer initLayer(Layer layer, String layerPrefix, Bounds layerBounds) {
    if (layer != null) {
      view.getCanvas().disposeLayer(layer);
    }
    return view.getCanvas().createLayer(layerPrefix + plotNumber, layerBounds);
  }

  private void animateTo(final double destDomainOrigin,
      final double destDomainLength, final PlotMovedEvent.MoveType eventType,
      final PortableTimerTask continuation, final boolean fence) {
    final DefaultXYPlot plot = this;
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
        
        // lerpFactor==1 means do just one step, better for low-performance devices (flash)
        double lerpFactor = 1;
        if (!ChronoscopeOptions.isLowPerformance()) {
          if (startTime == 0) {
            startTime = t.getTime();
          }
          double curTime = t.getTime();
          lerpFactor = (curTime - startTime) / 300;
          if (lerpFactor > 1) {
            lerpFactor = 1;
          }
        }
        
        isAnimating = true;

        final double domainCenter =
            (destDomainMid - srcDomain.midpoint()) * lerpFactor + srcDomain
                .midpoint();
        final double domainLength = srcDomain.length() * ((1 - lerpFactor) + (
            zoomFactor * lerpFactor));
        final double domainStart = domainCenter - domainLength / 2;
        visibleDomain.setEndpoints(domainStart, domainStart + domainLength);
        redraw(true);

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
          //Enlarged load to new data
          plotRenderer.cleanIncrementalData();
          redraw(true);
          fireChangeEvent();
        } else {
          lastFrame = true;
          plot.cancelHighlight();
          animationTimer.schedule(300);
        }
      }
    });

    animationTimer.schedule(10);
  }

  private void calcDomainWidths() {
    widestDomain = plotRenderer.calcWidestPlotDomain();
    visDomain = widestDomain.copy();
  }

  protected double fixDomainWidth(double span) {
    double max = Math.min(span, MAX_WIDTH_FACTOR * getDatasets().getDomainExtrema().length());
    double min = Math.max(span, MIN_WIDTH_FACTOR * getDatasets().getMinInterval());
    span = Math.min(max, span);
    span = Math.max(min, span);
    return span;
  }

    
  /**
   * Turns off an existing plot highlight.
   */
  @Export
  private void cancelHighlight() {
    setHighlight(0, 0);
  }

  private void clearDrawCaches() {
    bottomPanel.clearDrawCaches();
    topPanel.clearDrawCaches();
    rangePanel.clearDrawCaches();
  }
  
  @SuppressWarnings("unchecked")
  private TickFormatter<ChronoDate> getTickFormater() {
    TickFormatterFactory<ChronoDate> fact =  bottomPanel.getDomainAxisPanel().getTickFormatterFactory();
    return fact.findBestFormatter(getDomain().length());
  }
  
  private DateFormatter crosshairFmt;

  public void clearHoverLayer() {
    hoverLayer.save();
    hoverLayer.clearTextLayer("crosshair");
    hoverLayer.clearRect(0, 0, hoverLayer.getWidth(), hoverLayer.getHeight());
    hoverLayer.restore();
  }

  private void drawCrossHairs(Layer hoverLayer) {
    if (ChronoscopeOptions.isVerticalCrosshairEnabled() && hoverX > -1) {
      // hoverLayer.clearTextLayer("crosshair");
      hoverLayer.setFillColor(crosshairProperties.bgColor);
      hoverLayer.setStrokeColor(crosshairProperties.color);
      hoverLayer.setTransparency((float)crosshairProperties.transparency);

      if (hoverX > 0) {
        // consider painting crosshair line on overlay layer for Z order underneath (behind) the points
        hoverLayer.fillRect(hoverX-0.5, 0.5, 1, hoverLayer.getBounds().height);
        int hx = hoverX;
        double dx = windowXtoDomain(hoverX + plotBounds.x);
        ChronoDate cronoDate = new FastChronoDate(dx);

        if (ChronoscopeOptions.isCrosshairDateTimeFormat()) {
          if (crosshairFmt == null && !"auto".equals(ChronoscopeOptions.getCrosshairDateTimeFormat())) {
            crosshairFmt = DateFormatterFactory.getInstance().getDateFormatter(ChronoscopeOptions.getCrosshairDateTimeFormat());
          }
          String label;
          if (crosshairFmt != null) {
            label = crosshairFmt.format(cronoDate.getOffsetTime());
          } else {
            label = getTickFormater().formatCrosshair(cronoDate);
          }
          int labelWidth = hoverLayer.stringWidth(label, "Helvetica", "", "8pt");
          hx += dx < getDomain().midpoint() ? 1.0 : -1 - labelWidth;

          hoverLayer.drawText(hx, 0, label, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
        }

          int nearestPt = NO_SELECTION;
          int nearestSer = 0;
          int nearestDim = 0;
          NearestPoint nearest = this.nearestSingleton;
          
          if ("nearest".equals(crosshairProperties.pointSelection)) {

            double minNearestDist = MAX_FOCUS_DIST;
            
            for (int i = 0; i < datasets.size(); i++) {
              double domainX = windowXtoDomain(hoverX + plotBounds.x);
              double rangeY = windowYtoRange((int) (hoverY + plotBounds.y), i);
              findNearestPt(domainX, rangeY, i, DistanceFormula.XY, nearest);

              if (nearest.dist < minNearestDist) {
                nearestPt = nearest.pointIndex;
                nearestSer = i;
                minNearestDist = nearest.dist;
                nearestDim = nearest.dim;
              }
            }
          }
          if (hoverPoints != null) {
            List<LabelLayoutPoint> labelPointList = new ArrayList<LabelLayoutPoint>();
            for (int i = 0; i < hoverPoints.length; i++) {
              int hoverPoint = hoverPoints[i];
              if (nearestPt != NO_SELECTION && i != nearestSer) {
                continue;
              }
              if (hoverPoint > -1) {
                Dataset d = getDatasets().get(i);
                RangeAxis ra = getRangeAxis(i);
                DatasetRenderer r = getDatasetRenderer(i);
                for (int dim  : r.getLegendEntries(d)) {
                  if (nearestPt != NO_SELECTION && dim != nearestDim) {
                    continue;
                  }
                  // Tuple2D tuple = d.getFlyweightTuple(hoverPoint);
                  double realY = getDataCoord(i, hoverPoints[i], dim);
                  double y = r.getRangeValue(getDataTuple(i, hoverPoints[i]), dim);
                  double dy = rangeToScreenY(y, i);
                  String rLabel = "";
                  if (crosshairLabelsProperties.labelVisible) {
                      rLabel = DatasetLegendPanel.createDatasetLabel(this, i, -1, dim, true);
                  }
                  if (crosshairLabelsProperties.valueVisible) {
                      rLabel = ra.getFormattedLabel(realY) + " " + rLabel;
                  }
                  RenderState rs = new RenderState();
                  rs.setPassNumber(dim);
                  // GssProperties props = r.getLegendProperties(dim, rs);

                  hoverLayer.setStrokeColor(Color.BLACK);
                  hx = hoverX + (int) (dx < getDomain().midpoint() ? 1.0
                      : -1 - hoverLayer.stringWidth(rLabel, "Helvetica", "", "8pt"));

                  // Add the label positions for layout and display later
                  labelPointList.add(new LabelLayoutPoint(hx, dy, rLabel, crosshairLabelsProperties, hoverLayer));
                }
              }
            }
            // Sort the labels in to groups and display them in horizontal rows if needed.
            layoutAndDisplayLabels(labelPointList);
          }
        }

    }

    if (ChronoscopeOptions.isHorizontalCrosshairEnabled() && hoverY > -1) {
      hoverLayer.save();
      hoverLayer.setFillColor(crosshairProperties.bgColor);
      hoverLayer.setStrokeColor(crosshairProperties.color);
      hoverLayer.setTransparency((float)crosshairProperties.transparency);
      hoverLayer.fillRect(0, hoverY, hoverLayer.getBounds().width, 1);
      hoverLayer.restore();
    }
  }

  /**
     * Create a Label Layout Point Comparator
     * @return
     */
    private Comparator<LabelLayoutPoint> createLabelLayoutPointComparator() {
        Comparator<LabelLayoutPoint> comparator = new Comparator<LabelLayoutPoint>() {

            @Override
            public int compare(LabelLayoutPoint point, LabelLayoutPoint compare) {
                return (int) ((point.dy - compare.dy) * 100);
            }
        };
        return comparator;
    }


    private List<List<LabelLayoutPoint>> groupLabelList(List<LabelLayoutPoint> allLabelPoints) {
         List<List<LabelLayoutPoint>> labelGroups = new ArrayList<List<LabelLayoutPoint>>();
        if (allLabelPoints.size() > 1) {

            // Sort the labels by height
            Collections.sort(allLabelPoints, createLabelLayoutPointComparator());

            // create list of label groups
            List<LabelLayoutPoint> currentGroup = new ArrayList<LabelLayoutPoint>();

            double currentY = allLabelPoints.get(0).dy;

            currentGroup.add(allLabelPoints.get(0));
            labelGroups.add(currentGroup);

            int labelSize = 10; // make this dynamic according to font size.

            // loop through the labels and sort them a into groups.
            for (int i = 1; i < allLabelPoints.size(); i++) {

                double nextY = allLabelPoints.get(i).dy;
                if (Math.abs(currentY - nextY) > labelSize) {
                    // new group
                    currentGroup = new ArrayList<LabelLayoutPoint>();
                    labelGroups.add(currentGroup);

                }
                // add to current group
                currentGroup.add(allLabelPoints.get(i));
                currentY = nextY;
            }

        }
         return labelGroups;
    }

    /**
     * Treating Layout which LabelLayoutPoint may overlap and show them
     * @param allLabelPoints
     */
    private void layoutAndDisplayLabels(List<LabelLayoutPoint> allLabelPoints) {
       List<List<LabelLayoutPoint>> labelGroups =groupLabelList(allLabelPoints);
        if (labelGroups.size() > 0) {
            for (int i = 0; i < labelGroups.size(); i++) {
                List<LabelLayoutPoint> overlapList = labelGroups.get(i);
                if (overlapList.size() > 1) {
                    if (hoverX < plotBounds.width * 0.25) {
                        //All labels shows on the right between the region 0-0.25
                        LabelLayoutPoint point = overlapList.get(overlapList.size() - 1);
                        point.layer.setStrokeColor(point.gssProperties.color);
                        point.layer.drawText(point.hx, point.dy, point.labelText, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
                        double beforePointHx = point.hx;
                        int textLength = point.labelText.length() * 7;
                        for (int j = overlapList.size() - 2; j >= 0; j--) {
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            List<Number> infoList = drawLabelOnCrossHairRight(nextPoint, beforePointHx, textLength);
                            beforePointHx = infoList.get(0).doubleValue();
                            textLength = infoList.get(1).intValue();
                        }
                    } else if (hoverX > plotBounds.width * 0.75) {
                        //All labels shows on the left between the region 0.75-1
                        LabelLayoutPoint point = overlapList.get(0);
                        point.layer.setStrokeColor(point.gssProperties.color);
                        point.layer.drawText(point.hx, point.dy, point.labelText, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
                        double beforePointHx = point.hx;
                        for (int j = 1; j < overlapList.size(); j++) {
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            beforePointHx = drawLabelOnCrossHairLeft(nextPoint, beforePointHx);
                        }
                    } else if (hoverX < plotBounds.width * 0.5) {
                        // Shows label between the region 0.25-0.5
                        int middle;
                        if (overlapList.size() % 2 == 0) {
                            middle = overlapList.size() / 2 - 1;
                        } else {
                            middle = overlapList.size() / 2;
                        }
                        LabelLayoutPoint point = overlapList.get(middle);
                        double beforePointHx = point.hx;
                        int textLength = 0;
                        //show labels on the crossHair right
                        for (int j = middle; j >= 0; j--) {
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            List<Number> infoList = drawLabelOnCrossHairRight(nextPoint, beforePointHx, textLength);
                            beforePointHx = infoList.get(0).doubleValue();
                            textLength = infoList.get(1).intValue();
                        }
                        beforePointHx = point.hx;
                        for (int j = middle + 1; j < overlapList.size(); j++) {
                            //show labels on the crossHair left
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            beforePointHx = drawLabelOnCrossHairLeft(nextPoint, beforePointHx);
                        }
                    } else {
                        // Shows label between the region 0.5-0.75
                        int middle = overlapList.size() / 2;
                        LabelLayoutPoint point = overlapList.get(middle);
                        double beforePointHx = point.hx + point.labelText.length() * 7;
                        //show labels on the crossHair left
                        for (int j = middle; j < overlapList.size(); j++) {
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            beforePointHx = drawLabelOnCrossHairLeft(nextPoint, beforePointHx);
                        }
                        beforePointHx = point.hx + point.labelText.length() * 7;
                        int textLength = 0;
                        //show labels on the crossHair right
                        for (int j = middle - 1; j >= 0; j--) {
                            LabelLayoutPoint nextPoint = overlapList.get(j);
                            List<Number> infoList = drawLabelOnCrossHairRight(nextPoint, beforePointHx, textLength);
                            beforePointHx = infoList.get(0).doubleValue();
                            textLength = infoList.get(1).intValue();
                        }
                    }
                } else {
                    //Only one point
                    LabelLayoutPoint pendingPoint = overlapList.get(0);
                    pendingPoint.layer.setStrokeColor(pendingPoint.gssProperties.color);
                    // TODO - find out where the adjustment is made to take it out
                    pendingPoint.layer.drawText(pendingPoint.hx, pendingPoint.dy + 8, pendingPoint.labelText, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
                }
            }
        }
    }

    /**
     * Draw a Label On CrossHair Left
     * @param nextPoint
     * @param beforePointHx
     * @return
     */
    private double drawLabelOnCrossHairLeft(LabelLayoutPoint nextPoint, double beforePointHx) {
        nextPoint.layer.setStrokeColor(nextPoint.gssProperties.color);
        beforePointHx -= nextPoint.labelText.length() * 7;
        nextPoint.layer.drawText(beforePointHx, nextPoint.dy, nextPoint.labelText, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
        return beforePointHx;
    }

    /**
     * Draw a Label On Cross Hair Right
     * @param nextPoint
     * @param beforePointHx
     * @param textLength
     * @return
     */
    private List<Number> drawLabelOnCrossHairRight(LabelLayoutPoint nextPoint, double beforePointHx, int textLength) {
        nextPoint.layer.setStrokeColor(nextPoint.gssProperties.color);
        beforePointHx += textLength;
        nextPoint.layer.drawText(beforePointHx, nextPoint.dy, nextPoint.labelText, "Helvetica", "", "8pt", "crosshair", Cursor.CONTRASTED);
        textLength = nextPoint.labelText.length() * 7;
        List<Number> beforeInfor = new ArrayList<Number>();
        beforeInfor.add(0, beforePointHx);
        beforeInfor.add(1, textLength);
        return beforeInfor;
    }

    /**
     * Points need to display information,Location to be determined
     */
    private class LabelLayoutPoint {

        private double hx;
        private double dy;
        private String labelText;
        private GssProperties gssProperties;
        private Layer layer;

        LabelLayoutPoint(double hx, double dy, String labelText, GssProperties props, Layer layer) {
            this.hx = hx;
            this.dy = dy - 10; // move labels up an em or so
            this.labelText = labelText;
            this.gssProperties = props;
            this.layer = layer;
        }

        public double getDy() {
            return dy;
        }

        public void setDy(double dy) {
            this.dy = dy;
        }

        public GssProperties getGssProperties() {
            return gssProperties;
        }

        public void setGssProperties(GssProperties gssProperties) {
            this.gssProperties = gssProperties;
        }

        public double getHx() {
            return hx;
        }

        public void setHx(double hx) {
            this.hx = hx;
        }

        public String getLabelText() {
            return labelText;
        }

        public void setLabelText(String labelText) {
            this.labelText = labelText;
        }

        public Layer getLayer() {
            return layer;
        }

        public void setLayer(Layer layer) {
            this.layer = layer;
        }


    }



  public void clearOverlayLayer(Layer layer) {
    layer.save();
    layer.clearTextLayer("overlays");
    layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
    layer.restore();
  }

  /**
   * Draws the overlays (e.g. markers) onto the center plot.
   */
  private void drawOverlays(Layer layer) {
    layer.save();
    // clearOverlayLayer(layer);
    layer.setTextLayerBounds("overlays",
        new Bounds(0, 0, layer.getBounds().width, layer.getBounds().height));

    for (Overlay o : overlays) {
      if (null != o) { o.draw(layer, "overlays"); }
    }

    layer.restore();
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
        layer.save();
        layer.clear();
        layer.restore();
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
    //layer.clear();
    layer.fillRect(ux, 0, ex - ux, getInnerBounds().height);
    layer.restore();
    highlightDrawn = true;
  }

  private Interval fenceDomain(double destDomainOrig, double destDomainLength) {
    final double minDomain = widestDomain.getStart();
    final double maxDomain = widestDomain.getEnd();
    final double maxDomainLength = maxDomain - minDomain;
    final double minTickSize = bottomPanel.getDomainAxisPanel()
        .getMinimumTickSize();

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

    MipMap currMipMap = plotRenderer
        .getDrawableDataset(datasetIndex).currMipMap;

    // Find index of data point closest to the right of dataX at the current MIP level
    int closestPtToRight = Util.binarySearch(currMipMap.getDomain(), dataX);

    double sx = domainToScreenX(dataX, datasetIndex);
    double sy = rangeToScreenY(dataY, datasetIndex);
    Tuple2D tupleRight = currMipMap.getTuple(closestPtToRight);
    double rx = domainToScreenX(tupleRight.getDomain(), datasetIndex);
    double ry = rangeToScreenY(tupleRight, datasetIndex, 0);

    int nearestHoverPt;
    if (closestPtToRight == 0) {
      nearestHoverPt = closestPtToRight;
      np.dist = df.dist(sx, sy, rx, ry);
      np.dim = 0;
      for (int d = 1; d < currMipMap.getRangeTupleSize(); d++) {
        double dist2 = df.dist(sx, sy, rx,
            rangeToScreenY(tupleRight, datasetIndex, d));
        if (dist2 < np.dist) {
          np.dist = dist2;
          np.dim = d;
        }
      }
    } else {
      int closestPtToLeft = closestPtToRight - 1;
      Tuple2D tupleLeft = currMipMap.getTuple(closestPtToLeft);
      double lx = domainToScreenX(tupleLeft.getDomain(), datasetIndex);
      double ly = rangeToScreenY(tupleLeft, datasetIndex, 0);
      double lDist = df.dist(sx, sy, lx, ly);
      double rDist = df.dist(sx, sy, rx, ry);
      np.dim = 0;
      if (lDist <= rDist) {
        nearestHoverPt = closestPtToLeft;
        np.dist = lDist;
      } else {
        nearestHoverPt = closestPtToRight;
        np.dist = rDist;
      }
      for (int d = 1; d < currMipMap.getRangeTupleSize(); d++) {
        lDist = df.dist(sx, sy, lx,
            rangeToScreenY(tupleLeft, datasetIndex, d));
        rDist = df.dist(sx, sy, rx,
            rangeToScreenY(tupleRight, datasetIndex, d));
        if (lDist <= rDist && lDist <= np.dist) {
          nearestHoverPt = closestPtToLeft;
          np.dist = lDist;
          np.dim = d;
        } else if (rDist <= lDist && rDist <= np.dist) {
          nearestHoverPt = closestPtToRight;
          np.dist = rDist;
          np.dim = d;
        }
      }
    }
    np.pointIndex = nearestHoverPt;
  }

  private void fireChangeEvent() {
    handlerManager.fireEvent(new PlotChangedEvent(this, getDomain()));
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

  /**
   * If the Datasets extrema does not intersect the plot's domain, force the
   * plot's domain to be the Datasets extrema.
   */
  private void fixDomainDisjoint() {
    if (!datasets.getDomainExtrema().intersects(getDomain())) {
      getDomain().expand(datasets.getDomainExtrema());
      calcDomainWidths();
    }
  }

  
  public void init() {
    plotRenderer.reset();
    init(view, true);
    redraw(true);
  }

  public void init(View view) {
    init(view, true);
  }  
  
  private void init(View view, boolean forceNewRangeAxes) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");
    ArgChecker.isNotNull(plotRenderer, "plotRenderer");

    this.view = view;
    this.focus = null;
    plotRenderer.setPlot(this);
    plotRenderer.setView(view);

    initViewIndependent(datasets);

    initCrosshairs();

    if (stringSizer == null) {
      stringSizer = new StringSizer();
    }
    stringSizer.setCanvas(view.getCanvas());

    if (!plotRenderer.isInitialized()) {
      plotRenderer.init();
    } else {
      plotRenderer.sync();
      plotRenderer.resetMipMapLevels();
      plotRenderer.checkForGssChanges();
    }

    calcDomainWidths();

    ArgChecker.isNotNull(view.getCanvas(), "view.canvas");
    ArgChecker.isNotNull(view.getCanvas().getRootLayer(), "view.canvas.rootLayer");
    view.getCanvas().getRootLayer().setVisibility(true);

    initAuxiliaryPanel(bottomPanel, view);
    rangePanel.setCreateNewAxesOnInit(forceNewRangeAxes);
    initAuxiliaryPanel(rangePanel, view);
    
    GssProperties legendProps = view.getGssProperties(new GssElementImpl("axislegend", null), "");
    if (legendProps.gssSupplied && !legendOverridden) {
      topPanel.setEnabled(legendProps.visible);
    }

    /*
    if (!rangePanel.isInitialized()) {
      initAuxiliaryPanel(rangePanel, view);
    } else {
      rangePanel.bindDatasetsToRangeAxes();
    }
    */

    // FIXME: the top panel's initialization currently depends on the initialization
    // of the bottomPanel.  Remove this dependency if possible.
    initAuxiliaryPanel(topPanel, view);

    plotBounds = layoutAll();

    innerBounds = new Bounds(0, 0, plotBounds.width, plotBounds.height);

    clearDrawCaches();
    lastVisDomain = new Interval(0, 0);

    initLayers();

    background = new GssBackground(view);
    view.canvasSetupDone();
  }

  private void initCrosshairs() {
      GssElement crosshairElement = new GssElementImpl("crosshair", null);
      crosshairProperties = view.getGssProperties(crosshairElement, "");
      if (crosshairProperties.gssSupplied && crosshairProperties.visible) {
        ChronoscopeOptions.setVerticalCrosshairEnabled(true);
        if (!ChronoscopeOptions.isCrosshairDateTimeFormat()) {
          ChronoscopeOptions.setCrosshairDateTimeFormat(crosshairProperties.dateFormat);
        }
      }

      crosshairLabelsProperties = view.getGssPropertiesBySelector("crosshair labels");
      if (null == crosshairLabelsProperties) {
        crosshairLabelsProperties = view.getGssProperties(new GssElementImpl("labels", crosshairElement),"");
      }
  }

  private void initAuxiliaryPanel(AuxiliaryPanel panel, View view) {
    panel.setPlot(this);
    panel.setView(view);
    panel.setStringSizer(stringSizer);
    panel.init();
  }

  /**
   * Initializes the layers needed by the center plot.
   */
  private void initLayers() {
    view.getCanvas().getRootLayer().setLayerOrder(Layer.Z_LAYER_BACKGROUND);

    plotLayer = initLayer(plotLayer, LAYER_PLOT, plotBounds);

    hoverBounds = new Bounds(plotBounds.x, plotBounds.y, plotBounds.width, plotBounds.height);
    hoverLayer = initLayer(hoverLayer, LAYER_HOVER, hoverBounds);
    hoverLayer.setLayerOrder(Layer.Z_LAYER_HOVER);

    overlayLayer = initLayer(overlayLayer, LAYER_OVERLAY, plotBounds);
    overlayLayer.setLayerOrder(Layer.Z_LAYER_OVERLAY);

    topPanel.initLayer();
    rangePanel.initLayer();
    bottomPanel.initLayer();
  }

  /**
   * Returns true only if this plot is in a state such that animations (e.g.
   * zoom in, pan) are possible.
   */
  private boolean isAnimatable() {
    return this.visDomain.length() != 0.0;
  }
  
  private double computePlotHeight() {
    return view.getHeight() -  topPanel.getBounds().height - bottomPanel.getBounds().height;
  }
  
  /**
   * Perform layout on center plot and its surrounding panels.
   *
   * @return the bounds of the center plot
   */
  private Bounds layoutAll() {
    final double viewWidth = view.getWidth();

    plotBounds = new Bounds();
    topPanel.getBounds().height=0;
    bottomPanel.getBounds().height = MIN_BOTTOM_HEIGHT;

    // If center plot is too squished, remove legends 
    if (topPanel.isEnabled()) {
      topPanel.layout();
      if (computePlotHeight() < MIN_PLOT_HEIGHT) {
        topPanel.getBounds().height = 0;
        topPanel.setEnabled(false);
      }
    }

    // If center plot too squished, remove the overview
    bottomPanel.layout();
    if (bottomPanel.isOverviewVisible() && computePlotHeight() < MIN_PLOT_HEIGHT) {
      bottomPanel.setOverviewVisible(false);
      bottomPanel.getBounds().height = MIN_BOTTOM_HEIGHT;
    }

    rangePanel.setHeight(computePlotHeight());
    rangePanel.setWidth(viewWidth);
    rangePanel.layout();
    rangePanel.setPosition(0, topPanel.getBounds().height);
    
    // Set the center plot's bounds
    Bounds leftRangeBounds = rangePanel.getLeftSubPanel().getBounds();
    Bounds rightRangeBounds = rangePanel.getRightSubPanel().getBounds();
    plotBounds.x = leftRangeBounds.width;
    plotBounds.y = topPanel.getBounds().height;
    plotBounds.height = leftRangeBounds.height;
    plotBounds.width = viewWidth - leftRangeBounds.width - rightRangeBounds.width;

    // Set the positions of the auxiliary panels.
    topPanel.setPosition(0, 0);
    bottomPanel.setPosition(plotBounds.x, plotBounds.bottomY());
    bottomPanel.setWidth(plotBounds.width);

    return plotBounds;
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
        dataset.getX(Math.min(dataset.getNumSamples() - 1, pointIndex + zoomOffset))
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
   * Fills this.hoverPoints[] with {@link #NO_SELECTION} values. If
   * hoverPoints[] is null or not the same length as this.datsets.size(), it is
   * initialized to the correct size.
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
      setFocusAndNotifyView(focus.getDatasetIndex(), focus.getPointIndex(),
          focus.getDimensionIndex());
    }
  }

  private void setFocusAndNotifyView(int datasetIndex, int pointIndex, int nearestDim) {

    boolean damage = false;
    if (!multiaxis) {
      if (focus == null || focus.getDatasetIndex() != datasetIndex) {
        RangeAxis ra = getRangeAxis(datasetIndex);
        ra.getAxisPanel().setValueAxis(ra);
        damage = true;
      }
    }
    if (this.focus == null) {
      this.focus = new Focus();
    }
    this.focus.setDatasetIndex(datasetIndex);
    this.focus.setPointIndex(pointIndex);
    this.focus.setDimensionIndex(nearestDim);
    double domainX = plotRenderer.getDrawableDataset(datasetIndex).currMipMap.getDomain().get(pointIndex);
    this.focus.setDomainX(domainX);

    if (!multiaxis && damage) {
      damageAxes();
      rangePanel.layout();
    }
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
    int focusDatasetIdx, focusPointIdx, focusDim = 0;

    if (focus == null) {
      // If no data point currently has the focus, then set the focus point to
      // the point on dataset [0] that's closest to the center of the screen.
      focusDatasetIdx = 0;
      ds = datasets.get(focusDatasetIdx);
      MipMap mipMap = plotRenderer
          .getDrawableDataset(focusDatasetIdx).currMipMap;
      double domainCenter = visDomain.midpoint();
      focusPointIdx = Util.binarySearch(mipMap.getDomain(), domainCenter);
    } else {
      // some data point currently has the focus.
      focusDatasetIdx = focus.getDatasetIndex();
      focusPointIdx = focus.getPointIndex();
      focusDim = focus.getDimensionIndex();
      MipMap mipMap = plotRenderer
          .getDrawableDataset(focusDatasetIdx).currMipMap;
      focusPointIdx += n;

      if (focusPointIdx >= mipMap.size()) {
        focusDim++;
        if (focus.getDimensionIndex() < mipMap.getRangeTupleSize()) {
          focusPointIdx = 0;
        } else {
          focusDim = 0;
          ++focusDatasetIdx;
          if (focusDatasetIdx >= datasets.size()) {
            focusDatasetIdx = 0;
          }
        }
        focusPointIdx = 0;
      } else if (focusPointIdx < 0) {
        focusDim--;
        if (focusDim >= 0) {
          focusPointIdx =
              plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap.size()
                  - 1;
        } else {
          focusDim = 0;
          --focusDatasetIdx;
          if (focusDatasetIdx < 0) {
            focusDatasetIdx = datasets.size() - 1;
          }
          focusPointIdx =
              plotRenderer.getDrawableDataset(focusDatasetIdx).currMipMap.size()
                  - 1;
        }
      }

      ds = datasets.get(focusDatasetIdx);
    }

    MipMap currMipMap = plotRenderer
        .getDrawableDataset(focusDatasetIdx).currMipMap;
    Tuple2D dataPt = currMipMap.getTuple(focusPointIdx);
    double dataX = dataPt.getDomain();
    double dataY = dataPt.getRange0();
    ensureVisible(dataX, dataY, null);
    setFocusAndNotifyView(focusDatasetIdx, focusPointIdx, focusDim);
    redraw();
  }


    // TODO - these should go elsewhere

    @Export
    @Override
    public void showLegendLabels(boolean visible) {
        topPanel.setlegendLabelGssProperty(visible, null, null, null, null, null, null, null);
    }

    @Export
    @Override
    public void showLegendLabelsValues(boolean visible) {
        topPanel.setlegendLabelGssProperty(null, visible, null, null, null, null, null, null);
    }

    @Export
    @Override
    public void setLegendLabelsFontSize(int pixels) {
        topPanel.setlegendLabelGssProperty(null, null, pixels, null, null, null, null, null);
    }

    @Export
    @Override
    public void setLegendLabelsIconWidth(int pixels) {
        topPanel.setlegendLabelGssProperty(null, null, null, pixels, null, null, null, null);
    }

    @Export
    @Override
    public void setLegendLabelsIconHeight(int pixels) {
        topPanel.setlegendLabelGssProperty(null, null, null, null, pixels, null, null, null);
    }

    @Export
    @Override
    public void setLegendLabelsColumnWidth(int pixels) {
        topPanel.setlegendLabelGssProperty(null, null, null, null, null, pixels, null, null);
    }

    @Export
    @Override
    public void setLegendLabelsColumnCount(int count) {
        topPanel.setlegendLabelGssProperty(null, null, null, null, null, null, count, null);
    }
    
    @Export
    @Override
    public void setLegendLabelsColumnAlignment(boolean align) {
        topPanel.setlegendLabelGssProperty(null, null, null, null, null, null, null, align);
    }

    @Override
    public void clear() {
      // TODO: MCM check if there are objects which could be cleared
      animationContinuation = null;
      animationTimer = null;
      background = null;
      bottomPanel = null;
      changeTimer = null;
      crosshairFmt = null;
      crosshairLabelsProperties = null;
      crosshairProperties = null;
      if (datasets != null) {
        datasets.clear();
        datasets = null;
      }
      focus = null;
      handlerManager = null;
      hoverBounds = null;
      hoverLayer = null;
      innerBounds = null;
      lastVisDomain = null;
      overlayLayer = null;
      if (overlays != null) {
        overlays.clear();
        overlays = null;
      }
      plotBounds = null;
      plotRenderer = null;
      rangePanel = null;
      stringSizer = null;
      topPanel = null;
      if (view != null) {
        view.clear();
        view = null;
      }
      visDomain = null;
      widestDomain = null;
    }
}
