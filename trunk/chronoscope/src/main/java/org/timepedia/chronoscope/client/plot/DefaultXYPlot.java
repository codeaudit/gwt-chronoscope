package org.timepedia.chronoscope.client.plot;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.AxisPanel;
import org.timepedia.chronoscope.client.axis.DateAxis;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.axis.StockMarketDateAxis;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.UpdateableXYDataset;
import org.timepedia.chronoscope.client.data.XYDatasetListener;
import org.timepedia.chronoscope.client.data.HasRegions;
import org.timepedia.chronoscope.client.data.RegionLoadListener;
import org.timepedia.chronoscope.client.render.Background;
import org.timepedia.chronoscope.client.render.GssBackground;
import org.timepedia.chronoscope.client.render.ScalableXYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYLineRenderer;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.client.render.XYRenderer;
import org.timepedia.chronoscope.client.util.Nearest;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
public class DefaultXYPlot implements XYPlot, Exportable, XYDatasetListener,
        RegionLoadListener {

    public static int MAX_DRAWABLE_DATAPOINTS = 400;

    public static final double ZOOM_FACTOR = 1.50d;

    private static final int FRAMES = 8;

    private static int globalPlotNumber = 0;

    protected Bounds plotBounds;

    protected final boolean interactive;

    protected XYDataset[] dataSets = null;

    protected int focusSeries = -1;

    protected int focusPoint = -1;

    protected int hoverSeries = -1;

    protected int hoverPoint = -1;

    protected int currentMiplevels[];

    protected Vector selections;

    protected double domainMin, domainMax;

    protected boolean selection;

    protected int selStart;

    protected int selEnd;

    protected double domainStart;

    protected double domainEnd;

    protected final XYRenderer[] xyRenderers;

    protected Background background;

    protected DateAxis domainAxis;

    protected final Nearest nearestSingleton = new Nearest();

    protected AxisPanel domainPanel;

    protected AxisPanel rangePanelLeft;

    protected boolean drewVertical;

    protected RangeAxis[] axes;

    protected Layer overviewLayer;

    protected boolean overviewEnabled = true;

    protected double currentDomain;

    protected double domainOrigin;

    private boolean selectionMode;

    private double beginHighlight = Double.MIN_VALUE;

    private double endHighlight = Double.MIN_VALUE;

    private AxisPanel rangePanelRight;

    private boolean snapshotDrawn = false;

    private AxisPanel topPanel;

    private ArrayList overlays;

    private OverviewAxis overviewAxis;

    private boolean overviewDrawn = false;

    private LegendAxis legendAxis;

    private final HashMap<String, RangeAxis> axisMap = new HashMap<String, RangeAxis>();

    private boolean drewBackground;

    private boolean drewTop;

    private double lastCurrentDomain;

    private double lastDomainOrigin;

    private Layer plotLayer;

    private Layer verticalAxisLayer;

    private Layer domainLayer;

    private Bounds domainBounds;

    private Layer highLightLayer;

    private Bounds topBounds;

    private Layer topLayer;

    private boolean showLegend = true;

    private boolean isAnimating = false;

    private XYPlotRenderer plotRenderer;

    private View view;

    private Chart chart;

    private Bounds initialBounds;

    private int plotNumber = 0;

    private Bounds innerBounds;

    private PortableTimer animationTimer;

    private PortableTimerTask animationContinuation;

    private boolean domainAxisVisible = true;

    private boolean highlightDrawn;

    public DefaultXYPlot(Chart chart, XYDataset[] ds, boolean interactive,
                         Bounds initialBounds) {
        this.chart = chart;
        this.dataSets = ds;
        this.interactive = interactive;

        MAX_DRAWABLE_DATAPOINTS = 100 / ds.length;
        overlays = new ArrayList();
        xyRenderers = new XYRenderer[dataSets.length];
//        computeVisibleDomainStartEnd();
//        initializeDomain();

        plotRenderer = new ScalableXYPlotRenderer(this);
        this.initialBounds = initialBounds;
        plotNumber = globalPlotNumber++;
        setupDatasetListeners();
    }

    public DefaultXYPlot(Chart chart, XYDataset[] datasets, boolean interactive) {
        this(chart, datasets, interactive, null);
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

        if (animationTimer != null) {
            animationTimer.cancelTimer();
            if (animationContinuation != null) {
                animationContinuation.run(animationTimer);
            }
            animationTimer = null;
        }
        fence = false;
        double maxDomain = getDomainMax() - getDomainMin();
        final double destDom = fence ? Math.min(destinationDomain, maxDomain)
                : destinationDomain;
        double d = destinationOrigin;

// fence in origin
        if (fence) {
            if (destinationDomain >= maxDomain) {
                d = getDomainMin();
            } else if (destinationDomain < maxDomain) {
                if (destinationOrigin < getDomainMin()) {
                    d = getDomainMin();
                } else if (destinationOrigin + destDom > getDomainMax()) {
                    d = getDomainMax() - destDom;
                }
            }
        }

        final double destOrig = d;
        animationContinuation = continuation;

        animationTimer = view.createTimer(new PortableTimerTask() {
            double z = 1.0;

            final double zf = destDom / currentDomain;

            double domainCenter = domainOrigin + currentDomain / 2;

            final double sCenter = domainCenter;

            final double destCenter = destOrig + destDom / 2;

            final double startOrigin = domainOrigin;

            final double startDomain = currentDomain;

            double frames = 1;

            double startTime = 0;

            boolean correct = false;

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
        Iterator i = overlays.iterator();
        while (i.hasNext()) {
            Overlay o = (Overlay) i.next();
            double oPos = o.getDomainX();
            if (oPos >= domainOrigin && oPos <= domainOrigin + currentDomain) {
                if (o.isHit(x, y)) {
                    o.click(x, y);
                    return true;
                }
            }
        }
        return legendAxis.click(x, y);
    }

    /**
     * Any cached drawings of this axis are flushed and redrawn on next update
     */
    public void damageAxes(ValueAxis axis) {
        drewVertical = false;
    }

    public double domainToScreenX(double dataX, int seriesNum) {
        return userToScreenX(getDomainAxis().dataToUser(dataX));
    }

    public double domainToWindowX(double dataX, int seriesNum) {
        return userToWindowX(getDomainAxis().dataToUser(dataX));
    }

    public boolean ensureVisible(int seriesNum, int pointNum,
                                 PortableTimerTask callback) {
        return ensureVisible(dataSets[seriesNum].getX(pointNum),
                dataSets[seriesNum].getY(pointNum), callback);
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

    public Nearest findNearestWithin(Nearest nearestResult, double domainX,
                                     double rangeY, int seriesNum, int within) {
        double cx = domainToScreenX(domainX, seriesNum);
        double cy = rangeToScreenY(rangeY, seriesNum);

         int where = Util.binarySearch(dataSets[seriesNum], domainX,
                currentMiplevels[seriesNum]);

        double x1 = domainToScreenX(
                dataSets[seriesNum].getX(where, currentMiplevels[seriesNum]),
                seriesNum);
        double y1 = rangeToScreenY(
                dataSets[seriesNum].getY(where, currentMiplevels[seriesNum]),
                seriesNum);
        double x2, y2;
        if (where + 1 < dataSets[seriesNum]
                .getNumSamples(currentMiplevels[seriesNum])) {
            x2 = domainToScreenX(
                    dataSets[seriesNum].getX(where + 1, currentMiplevels[seriesNum]),
                    seriesNum);
            y2 = rangeToScreenY(
                    dataSets[seriesNum].getY(where + 1, currentMiplevels[seriesNum]),
                    seriesNum);
        } else {
            x2 = x1;
            y2 = y1;
        }

        double d1 = dist(x1, y1, cx, cy);
        double d2 = dist(x2, y2, cx, cy);
        nearestResult.nearest = -1;
        nearestResult.series = seriesNum;

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

    public Chart getChart() {
        return chart;
    }

    public int getCurrentDatasetLevel(int seriesNum) {
        return currentMiplevels[seriesNum];
    }

    public double getCurrentDomain() {
        return currentDomain;
    }

    public int getCurrentMipLevel(int seriesNum) {
        return currentMiplevels[seriesNum];
    }

    public XYDataset getDataset(int i) {
        return dataSets[i];
    }

    public double getDataX(int serNum, int serPer) {
        return dataSets[serNum].getX(serPer, currentMiplevels[serNum]);
    }

    public double getDataY(int serNum, int serPer) {
        return dataSets[serNum].getY(serPer, currentMiplevels[serNum]);
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

    public int getFocusPoint() {
        return focusPoint;
    }

    public int getFocusSeries() {
        return focusSeries;
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
                .binarySearch(dataSets[series], domainX, currentMiplevels[series]);
    }

    public int getNumAnimationFrames() {
        return FRAMES;
    }

    public int getNumDatasets() {
        return dataSets == null ? 0 : dataSets.length;
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
    public RangeAxis getRangeAxis(int seriesNum) {
        return axes[seriesNum];
    }

    public int getRangeAxisCount() {
        return axisMap.size();
    }

    public XYRenderer getRenderer(int seriesNum) {
        return xyRenderers[seriesNum];
    }

    public double getSelectionBegin() {
        return beginHighlight;
    }

    public double getSelectionEnd() {
        return endHighlight;
    }

    public int getSeriesCount() {
        return dataSets.length;
    }

    public String getSeriesLabel(int i) {
        return dataSets[i].getRangeLabel() + getRangeAxis(i)
                .getLabelSuffix(getRangeAxis(i).getRange());
    }

    public boolean hasAxis(ValueAxis theAxis) {
        return topPanel.contains(theAxis) || domainPanel.contains(theAxis)
                || rangePanelLeft.contains(theAxis) || rangePanelRight
                .contains(theAxis);
    }

    public void init(View view) {

        this.view = view;
        focusPoint = 0;
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
        axes = new RangeAxis[dataSets.length];

        autoAssignDatasetAxes();

        topPanel = new AxisPanel("topPanel" + plotNumber, AxisPanel.TOP);
        legendAxis = new LegendAxis(this, topPanel, "My graph");
        if (showLegend) {
            topPanel.add(legendAxis);
        }

        computePlotBounds();
        initDefaultRenderers();
        initDatasetLevels();

        background = new GssBackground(this);

        computeDomainMinMax();
        computeVisibleDomainStartEnd();
        initializeDomain();

        clearDrawCaches();

        lastDomainOrigin = domainOrigin;
        lastCurrentDomain = currentDomain;

        initLayers();
        view.canvasSetupDone();
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

        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
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
            maxZoomToPoint(nearNum, nearSer);
            return true;
        } else {
            return false;
        }
    }

    public void maxZoomToFocus() {
        if (focusPoint != -1) {
            maxZoomToPoint(focusPoint, focusSeries);
        }
    }

    public void nextFocus() {

        if (focusSeries == -1) {
            setFocusPointImpl(0, 0);
        } else {
            focusPoint++;
            if (focusPoint >= dataSets[focusSeries]
                    .getNumSamples(currentMiplevels[focusSeries])) {
                focusPoint = 0;
                focusSeries++;
                if (focusSeries >= dataSets.length) {
                    focusSeries = 0;
                }
            }
            setFocusPointImpl(focusSeries, focusPoint);
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
        damageAxes(getRangeAxis(getSeriesNumber(dataset)));
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

                               final double rangeY, final int seriesNum) {

        if (ensureVisible(domainX, rangeY, new PortableTimerTask() {

            public void run(PortableTimer timer) {
                view.openInfoWindow(html,
                        chart.domainToWindowX(DefaultXYPlot.this, domainX, seriesNum),
                        chart.rangeToWindowY(DefaultXYPlot.this, rangeY, seriesNum) + 5);
            }
        })) {

        } else {
            view.openInfoWindow(html,
                    chart.domainToWindowX(DefaultXYPlot.this, domainX, seriesNum),
                    chart.rangeToWindowY(DefaultXYPlot.this, rangeY, seriesNum) + 5);
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

        if (focusSeries == -1) {

            setFocusPointImpl(0, 0);
        } else {
            focusPoint--;
            if (focusPoint < 0) {
                focusSeries--;
                if (focusSeries < 0) {
                    focusSeries = dataSets.length - 1;
                }
                focusPoint =
                        dataSets[focusSeries].getNumSamples(currentMiplevels[focusSeries])
                                - 1;
            }
            setFocusPointImpl(focusSeries, focusPoint);
        }
        redraw();
    }

    public void prevZoom() {

        pushHistory();
        double nDomain = currentDomain * ZOOM_FACTOR;
        animateTo(getDomainCenter() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
    }

    public double rangeToScreenY(double dataY, int seriesNum) {
        return userToScreenY(getRangeAxis(seriesNum).dataToUser(dataY));
    }

    public double rangeToWindowY(double dataY, int seriesNum) {
        return userToWindowY(getRangeAxis(seriesNum).dataToUser(dataY));
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

    public void setAutoZoomVisibleRange(int dataSet, boolean autoZoom) {
        axes[dataSet].setAutoZoomVisibleRange(autoZoom);
    }

    public void setAxisForDataset(RangeAxis ra, int datasetNum) {
        axes[datasetNum] = ra;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public void setCurrentDatasetLevel(int seriesNum, int mipLevel) {
        if (currentMiplevels[seriesNum] != mipLevel) {
            hoverPoint = -1;
            hoverSeries = -1;
            // TODO: maybe adjust to nearest one in next level of detail
            focusPoint = -1;
            focusSeries = -1;
        }
        currentMiplevels[seriesNum] = mipLevel;
    }

    public void setCurrentDomain(double currentDomain) {
        this.currentDomain = currentDomain;
    }

    public void setDataset(int i, XYDataset d) {
        dataSets[i] = d;
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

    public void setFocus(int series, int point) {
        this.focusSeries = series;
        this.focusPoint = point;
    }

    public void setFocusPoint(int focusSeries, int focusPoint) {
        setFocusPointImpl(focusSeries, focusPoint);
        redraw();
    }

    public void setFocusPoint(int i, int point, int mip) {
        currentMiplevels[i] = mip;
        setFocusPoint(i, point);
    }

    public boolean setFocusXY(int x, int y) {
        int lastFocus = focusPoint;
        int lastFocusSeries = focusSeries;
        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
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
            setFocusPointImpl(nearSer, nearNum + 1);

            if (lastFocusSeries != focusSeries || lastFocus != focusPoint) {

                redraw();
            }
            return true;
        } else {

            setFocusPointImpl(-1, -1);
            if (lastFocusSeries != focusSeries || lastFocus != focusPoint) {
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
//        drawHighlight(highLightLayer, highLightLayer);
    }

    public boolean setHover(int x, int y) {

        int lastHover = hoverPoint;
        int lastHoverSeries = hoverSeries;
        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
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

    public void setRenderer(int seriesNum, XYRenderer r) {
        xyRenderers[seriesNum] = r;
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
//    plotLayer.setFillColor("#FF0000");
//    plotLayer.fillRect(0, 0, 50, 50);

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
                        new Bounds(plotBounds.x, 0, plotBounds.width, topBounds.height),
                        false);
                drewTop = true;
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
     * Convert a value in user coordinates [0,1] to plot region screen-space coordinates [0, plotBounds.width]
     *
     * @param userX
     * @return
     */
    public double userToScreenX(double userX) {
        return userX * plotBounds.width;
    }

    /**
     * COnvert a value in user coordinates [0,1] to window screen-space coordinates [plotBounds.x, plotBounds.width]
     *
     * @param userX
     * @return
     */
    public double userToWindowX(double userX) {
        return userToScreenX(userX) + plotBounds.x;
    }

    public double userToWindowY(double userY) {
        return userToScreenY(userY) + plotBounds.y;
    }

    public void zoomToHighlight() {
        final double newOrigin = beginHighlight;

        double newdomain = endHighlight - beginHighlight;
        pushHistory();
        animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
    }

    protected void autoAssignDatasetAxes() {
        int rangeAxisCount = -1;
        for (int i = 0; i < dataSets.length; i++) {
            RangeAxis ra = (RangeAxis) axisMap.get(dataSets[i].getAxisId());
            if (ra == null) {
                rangeAxisCount++;
                ra = new RangeAxis(chart, dataSets[i].getRangeLabel(),
                        dataSets[i].getAxisId(), i, dataSets[i].getRangeBottom(),
                        dataSets[i].getRangeTop(),
                        rangeAxisCount % 2 == 0 ? rangePanelLeft : rangePanelRight);
                axisMap.put(ra.getAxisId(), ra);
                if (rangeAxisCount % 2 == 0) {
                    rangePanelLeft.add(ra);
                } else {
                    rangePanelRight.add(ra);
                }
            } else {
                ra.setRange(Math.min(ra.getRangeLow(), dataSets[i].getRangeBottom()),
                        Math.max(ra.getRangeHigh(), dataSets[i].getRangeTop()));

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
//        layer.setLayerAlpha(0.2f);
        layer.setTransparency(0.2f);
        layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
        layer.fillRect(ux, 0, ex - ux, getInnerPlotBounds().height);
        layer.restore();
        highlightDrawn = true;
    }

    protected void pushHistory() {
        Chronoscope.pushHistory();
    }

    protected double windowXtoDomain(double x, int seriesNum) {
        return getDomainAxis().userToData(windowXtoUser(x));
    }

    public double windowXtoUser(double x) {
        return (x - plotBounds.x) / plotBounds.width;
    }

    protected double windowYtoRange(int y, int seriesNum) {
        return getRangeAxis(seriesNum).userToData(windowYtoUser(y));
    }

    private double windowYtoUser(int y) {
        return (plotBounds.height - (y - plotBounds
                .y)) / plotBounds.height;
    }

    private void clearDrawCaches() {
        drewVertical = false;
        overviewDrawn = false;
        drewTop = false;
    }

    private void computeDomainMinMax() {
        domainMin = Double.POSITIVE_INFINITY;
        domainMax = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < dataSets.length; i++) {
            double min = dataSets[i].getDomainBegin();
            domainMin = Math.min(domainMin, min);
            double max = dataSets[i].getDomainEnd();
            domainMax = Math.max(domainMax, max);
        }
    }

    private void computePlotBounds() {
        plotBounds = initialBounds == null ? new Bounds(0, 0,
                this.view.getViewWidth(), this.view.getViewHeight())
                : new Bounds(initialBounds);

        innerBounds = new Bounds(plotBounds);
        // TODO: only in snapshot
        if (interactive) {
            plotBounds.x = rangePanelLeft.getWidth();
            plotBounds.width -= plotBounds.x;
            plotBounds.y += topPanel.getHeight();
            if (domainAxisVisible && domainPanel.getAxisCount() > 0) {
                plotBounds.height -= domainPanel.getHeight() + topPanel.getHeight();
            }
            if (rangePanelRight.getAxisCount() > 0) {
                plotBounds.width -= rangePanelRight.getWidth();
            }
        }

        innerBounds.x = 0;
        innerBounds.y = 0;
    }

    private void computeVisibleDomainStartEnd() {
        domainStart = Util.computeDomainStart(this, dataSets);
        domainEnd = Util.computeDomainEnd(this, dataSets);
    }

    private double dist(double x1, double y1, double cx, double cy) {
        //TODO: we now ignore y dist to make hover easier
        return Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy));
//    return Math.abs(x1-cx);
    }

    private void drawOverlays(Layer overviewLayer) {
        overviewLayer.save();
        overviewLayer.clearTextLayer("overlays");
        overviewLayer.setTextLayerBounds("overlays", new Bounds(0, 0,
                overviewLayer.getBounds().width, overviewLayer.getBounds().height));
        Iterator i = overlays.iterator();
        while (i.hasNext()) {
            Overlay o = (Overlay) i.next();
            double oPos = o.getDomainX();
            if (oPos >= domainOrigin && oPos <= domainOrigin + currentDomain) {
                o.draw(overviewLayer, "overlays");
            }
        }
        overviewLayer.restore();
    }

    private void drawPlot() {
        double doChange = domainOrigin - lastDomainOrigin;
        double cdChange = currentDomain - lastCurrentDomain;

        double numPixels = doChange / currentDomain * plotLayer.getWidth();
        // disabled for now, implement smooth local scrolling by rendering
        // a chart with overdraw clipped to the view, and scroll overdraw regions into view
        // as needed
        if (false && cdChange == 0 && numPixels < plotLayer.getWidth() / 2) {
            plotLayer.setScrollLeft((int) (plotLayer.getScrollLeft() - numPixels));
            domainOrigin += doChange;
        } else {
            plotLayer.setScrollLeft(0);

            background.paint(this, plotLayer, domainOrigin, currentDomain);
            drewBackground = true;

            // reset the visible RangeAxis ticks if it's been zoomed
            for (int i = 0; i < dataSets.length; i++) {
                axes[i].initVisibleRange();
            }

            plotRenderer.drawDatasets();
        }
    }

    private int getSeriesNumber(XYDataset dataset) {
        for (int i = 0; i < dataSets.length; i++) {
            if (dataSets[i] == dataset) {
                return i;
            }
        }
        return 0; // TODO: silent fail
    }

    private void initDatasetLevels() {
        currentMiplevels = new int[dataSets.length];
        for (int i = 0; i < currentMiplevels.length; i++) {
            currentMiplevels[i] = 0;
        }
    }

    private void initDefaultRenderers() {
        for (int i = 0; i < dataSets.length; i++) {
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

    private void maxZoomToPoint(int point, int series) {
        pushHistory();

        XYDataset dataset = dataSets[series];
        point = Util.binarySearch(dataset,
                dataset.getX(point, currentMiplevels[series]), 0);

        final double newOrigin = dataset.getX(Math.max(0, point - 10));
        double newdomain =
                dataset.getX(Math.min(dataset.getNumSamples(), point + 10)) - newOrigin;

        animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
    }

    private void setFocusPointImpl(int focusSeries, int focusPoint) {
        this.focusSeries = focusSeries;
        this.focusPoint = focusPoint;
        view.fireFocusEvent(this, focusSeries, focusPoint);
    }

    private void setupDatasetListeners() {
        for (int i = 0; i < dataSets.length; i++) {
            XYDataset dataSet = dataSets[i];
            if (dataSet instanceof UpdateableXYDataset) {
                ((UpdateableXYDataset) dataSet).addXYDatasetListener(this);
            }
            if (dataSet instanceof HasRegions) {
                ((HasRegions) dataSet).addRegionLoadListener(this);
            }
        }
    }

    public double userToScreenY(double userY) {
        return plotBounds.height - userY * plotBounds.height;
    }
}
