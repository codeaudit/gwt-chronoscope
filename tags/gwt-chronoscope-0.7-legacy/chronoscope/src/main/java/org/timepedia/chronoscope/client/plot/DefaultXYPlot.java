package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.*;
import org.timepedia.chronoscope.client.axis.*;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.*;
import org.timepedia.chronoscope.client.util.Nearest;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * A DefaultXYPlot is responsible for drawing the main chart area (excluding axes),
 * mapping one or more datasets from (domain,range) space to (x,y) screen space
 * by delegating to one or more ValueAxis implementations. Drawing for each dataset
 * is delegated to Renderers. A plot also maintains state like the current
 * selection and focus point.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 * @gwt.exportPackage chronoscope
 */
public class DefaultXYPlot implements XYPlot, Exportable {


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
    public static int MAX_DRAWABLE_DATAPOINTS = 400;
    private boolean selectionMode;
    protected boolean selection;
    protected int selStart;
    protected int selEnd;
    protected double domainStart;
    protected double domainEnd;
    protected final XYRenderer[] xyRenderers;
    protected Background background;
    protected DateAxis domainAxis;
    private double beginHighlight = Double.MIN_VALUE;
    private double endHighlight = Double.MIN_VALUE;
    protected final Nearest nearestSingleton = new Nearest();
    protected AxisPanel domainPanel;
    protected AxisPanel rangePanelLeft;
    protected boolean drewVertical;
    private AxisPanel rangePanelRight;
    private boolean snapshotDrawn = false;
    private AxisPanel topPanel;


    protected RangeAxis[] axes;
    private ArrayList overlays;
    private OverviewAxis overviewAxis;
    private boolean overviewDrawn = false;
    private LegendAxis legendAxis;
    private final HashMap axisMap = new HashMap();
    private boolean drewBackground;
    private boolean drewTop;
    protected Layer overviewLayer;
    private double lastCurrentDomain;
    private double lastDomainOrigin;
    private Layer plotLayer;
    private Layer verticalAxisLayer;
    private Layer domainLayer;
    private Bounds domainBounds;
    private Layer hightLightLayer;
    private Bounds topBounds;
    private Layer topLayer;


    protected boolean overviewEnabled = true;
    private boolean showLegend = true;
    private boolean isAnimating = false;
    public static final double ZOOM_FACTOR = 1.50d;
    private static final int FRAMES = 8;

    private XYPlotRenderer plotRenderer;
    private View view;

    protected double currentDomain;
    protected double domainOrigin;
    private Chart chart;
    private Bounds initialBounds;
    private int plotNumber = 0;
    private static int globalPlotNumber = 0;
    private Bounds innerBounds;
    private PortableTimer animationTimer;

    public boolean isDomainAxisVisible() {
        return domainAxisVisible;
    }

    public void setDomainAxisVisible(boolean visible) {
        this.domainAxisVisible = visible;
    }

    private boolean domainAxisVisible = true;

    public DefaultXYPlot(Chart chart, XYDataset[] ds, boolean interactive, Bounds initialBounds) {
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
    }

    public DefaultXYPlot(Chart chart, XYDataset[] datasets, boolean interactive) {
        this(chart, datasets, interactive, null);
    }

    public double getDomainOrigin() {
        return domainOrigin;
    }

    public void setDomainOrigin(double domainOrigin) {
        this.domainOrigin = domainOrigin;
    }

    public double getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(double currentDomain) {
        this.currentDomain = currentDomain;
    }


    /**
     * @param over
     * @gwt.export
     */
    public void addOverlay(Overlay over) {

        overlays.add(over);
        over.setPlot(this);
    }

    public void removeOverlay(Overlay over) {
        overlays.remove(over);
    }


    /**
     * @gwt.export
     */
    public void redraw() {
        update();
        lastCurrentDomain = currentDomain;
        lastDomainOrigin = domainOrigin;
        view.flipCanvas();
    }

    public void init(View view) {

        this.view = view;
        focusPoint = 0;
        this.view.getCanvas().setVisibility(true);

        domainPanel = new AxisPanel("domainAxisLayer" + plotNumber, AxisPanel.BOTTOM);
        domainAxis = new DateAxis(this, domainPanel);

        if (domainAxisVisible) {
            domainPanel.add(domainAxis);
        }

        if (overviewEnabled) {
            overviewAxis = new OverviewAxis(this, domainPanel, "Overview");
            domainPanel.add(overviewAxis);
        }

        axisMap.clear();

        rangePanelLeft = new AxisPanel("rangeAxisLayerLeft" + plotNumber, AxisPanel.LEFT);
        rangePanelRight = new AxisPanel("rangeAxisLayerRight" + plotNumber, AxisPanel.RIGHT);
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
    }

    private void clearDrawCaches() {
        drewVertical = false;
        overviewDrawn = false;
        drewTop = false;
    }

    private void initializeDomain() {
        domainOrigin = domainMin;
        currentDomain = domainMax - domainOrigin;
    }

    private void computeVisibleDomainStartEnd() {
        domainStart = Util.computeDomainStart(this, dataSets);
        domainEnd = Util.computeDomainEnd(this, dataSets);
    }

    private void computeDomainMinMax() {
        domainMin = Double.MAX_VALUE;
        domainMax = Double.MIN_VALUE;
        for (int i = 0; i < dataSets.length; i++) {
            double min = dataSets[i].getX(0);
            domainMin = Math.min(domainMin, min);
            double max = dataSets[i].getX(dataSets[i].getNumSamples() - 1);
            domainMax = Math.max(domainMax, max);
        }
    }

    private void initDatasetLevels() {
        currentMiplevels = new int[dataSets.length];
        for (int i = 0; i < currentMiplevels.length; i++) {
            currentMiplevels[i] = 0;
        }
    }

    private void initDefaultRenderers() {
        for (int i = 0; i < dataSets.length; i++) {
            if (xyRenderers[i] == null)
                xyRenderers[i] = new XYLineRenderer(i);
        }
    }

    private void computePlotBounds() {
        plotBounds = initialBounds == null ? new Bounds(0, 0, this.view.getViewWidth(), this.view.getViewHeight()) :
                new Bounds(initialBounds);

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

        innerBounds = new Bounds(plotBounds);
        innerBounds.x = 0;
        innerBounds.y = 0;
    }

    private void autoAssignDatasetAxes() {
        for (int i = 0; i < dataSets.length; i++) {
            RangeAxis ra = (RangeAxis) axisMap.get(dataSets[i].getAxisId());
            if (ra == null) {
                ra = new RangeAxis(chart, dataSets[i].getRangeLabel(), dataSets[i].getAxisId(), i,
                        dataSets[i].getRangeBottom(), dataSets[i].getRangeTop(),
                        i % 2 == 0 ? rangePanelLeft : rangePanelRight);
                if (i % 2 == 0) {
                    rangePanelLeft.add(ra);
                } else {
                    rangePanelRight.add(ra);
                }
            }

            axes[i] = ra;
        }
    }

    public void setInitialBounds(Bounds initialBounds) {
        this.initialBounds = initialBounds;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public Layer getPlotLayer() {
        return view.getCanvas().createLayer("plotLayer" + plotNumber, plotBounds);
    }

    private void initLayers() {
        Canvas backingCanvas = view.getCanvas();

        backingCanvas.setLayerOrder(Layer.Z_LAYER_BACKGROUND);

        if (plotLayer != null) {
            backingCanvas.disposeLayer(plotLayer);
        }
        plotLayer = backingCanvas.createLayer("plotLayer" + plotNumber, plotBounds);

        if (interactive) {

            if (overviewEnabled) {
                if (overviewLayer != null) {
                    backingCanvas.disposeLayer(overviewLayer);
                }

                overviewLayer = backingCanvas.createLayer("overviewLayer" + plotNumber, plotBounds);
                overviewLayer.setVisibility(false);
            }

            Bounds layerBounds = new Bounds(0, plotBounds.y, view.getViewWidth(), rangePanelLeft.getHeight());
            if (verticalAxisLayer != null) {
                backingCanvas.disposeLayer(verticalAxisLayer);
            }
            verticalAxisLayer = backingCanvas.createLayer("verticalAxis" + plotNumber, layerBounds);
            verticalAxisLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
            verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
            verticalAxisLayer.clearRect(0, 0, verticalAxisLayer.getWidth(), verticalAxisLayer.getHeight());

            domainBounds = new Bounds(0, plotBounds.y + plotBounds.height, view.getViewWidth(),
                    domainPanel.getHeight());

            if (domainLayer != null) {
                backingCanvas.disposeLayer(domainLayer);
            }

            domainLayer = backingCanvas.createLayer("domainAxis" + plotNumber, domainBounds);
            domainLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
            hightLightLayer = backingCanvas.createLayer("highlight" + plotNumber, plotBounds);
            hightLightLayer.setLayerOrder(Layer.Z_LAYER_HIGHLIGHT);
            topBounds = new Bounds(0, 0, view.getViewWidth(), topPanel.getHeight());
            topLayer = backingCanvas.createLayer("topLayer" + plotNumber, topBounds);
            topLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
        }


    }

    /**
     * @gwt.export
     */
    public void reloadStyles() {
        init(view);
        overviewDrawn = false;
        double so = getDomainOrigin();
        double scd = getCurrentDomain();
        ArrayList oldOverlays = overlays;
        overlays = new ArrayList();

        initializeDomain();
        redraw();
        setDomainOrigin(so);
        setCurrentDomain(scd);
        overlays = oldOverlays;
    }

    public void update() {

        Canvas backingCanvas = view.getCanvas();
        backingCanvas.beginFrame();


        plotLayer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
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

        if (interactive) {

            if (!overviewDrawn && overviewEnabled) {

                overviewLayer.clearRect(0, 0, overviewLayer.getWidth(), overviewLayer.getHeight());

                overviewLayer.drawImage(plotLayer, 0, 0, overviewLayer.getWidth(), overviewLayer.getHeight());
                overviewDrawn = true;
            }

            boolean drawVertical = !drewVertical;
            for (int i = 0; i < axes.length; i++) {
                drawVertical = drawVertical || axes[i].isAutoZoomVisibleRange();
            }

            if (drawVertical) {
                verticalAxisLayer.setFillColor("rgba(0,0,0,0)");
                verticalAxisLayer.clearRect(0, 0, verticalAxisLayer.getWidth(), verticalAxisLayer.getHeight());

                drawAxisPanel(verticalAxisLayer, rangePanelLeft, new Bounds(0, 0, rangePanelLeft.getWidth(),
                        rangePanelLeft.getHeight()), false);
                if (rangePanelRight.getAxisCount() > 0) {
                    Bounds rightBounds = new Bounds(plotBounds.x + plotBounds.width, 0, rangePanelRight.getWidth(),
                            rangePanelRight.getHeight());


                    drawAxisPanel(verticalAxisLayer, rangePanelRight, rightBounds, false);

                }
                drewVertical = true;
            }

            if (domainAxisVisible && domainPanel.getAxisCount() > 0) {
                drawAxisPanel(domainLayer, domainPanel, new Bounds(plotBounds.x, 0, plotBounds.width,
                        domainBounds.height), false);
            }

            if (!drewTop && topPanel.getAxisCount() > 0) {

                drawAxisPanel(topLayer, topPanel, new Bounds(plotBounds.x, 0, plotBounds.width, topBounds.height),
                        false);
                drewTop = true;
            }

            drawOverlays(plotLayer);
            drawHighlight(hightLightLayer);
        }


        backingCanvas.endFrame();


    }

    public void setAutoZoomVisibleRange(int dataSet, boolean autoZoom) {
        axes[dataSet].setAutoZoomVisibleRange(autoZoom);
    }

    private void drawOverlays(Layer overviewLayer) {
        overviewLayer.clearTextLayer("overlays");
        Iterator i = overlays.iterator();
        while (i.hasNext()) {
            Overlay o = (Overlay) i.next();
            double oPos = o.getDomainX();
            if (oPos >= domainOrigin && oPos <= domainOrigin + currentDomain) {
                o.draw(overviewLayer, "overlays");
            }
        }
    }

    public void showOverview() {

    }


    public double domainToScreenX(double dataX, int seriesNum) {
        return getDomainAxis().dataToUser(dataX) * plotBounds.width;
    }

    public double domainToWindowX(double dataX, int seriesNum) {
        return domainToWindowX(dataX, seriesNum) + plotBounds.x;
    }

    public double rangeToScreenY(double dataY, int seriesNum) {
        return plotBounds.height - getRangeAxis(seriesNum).dataToUser(dataY) * plotBounds.height;
    }


    public double rangeToWindowY(double dataY, int seriesNum) {
        return rangeToScreenY(dataY, seriesNum) + plotBounds.y;
    }


    public void nextFocus() {

        if (focusSeries == -1) {
            _setFocusPoint(0, 0);
        } else {
            focusPoint++;
            if (focusPoint >= dataSets[focusSeries].getNumSamples(currentMiplevels[focusSeries])) {
                focusPoint = 0;
                focusSeries++;
                if (focusSeries >= dataSets.length) {
                    focusSeries = 0;
                }
            }
            _setFocusPoint(focusSeries, focusPoint);

        }
        redraw();
    }

    public void prevFocus() {

        if (focusSeries == -1) {

            _setFocusPoint(0, 0);
        } else {
            focusPoint--;
            if (focusPoint < 0) {
                focusSeries--;
                if (focusSeries < 0) {
                    focusSeries = dataSets.length - 1;
                }
                focusPoint = dataSets[focusSeries].getNumSamples(currentMiplevels[focusSeries]) - 1;
            }
            _setFocusPoint(focusSeries, focusPoint);
        }
        redraw();
    }

    public double getDomainMin() {
        return domainMin;
    }

    public double getDomainMax() {
        return domainMax;
    }

    public Bounds getPlotBounds() {
        return plotBounds;
    }

    public Bounds getInnerPlotBounds() {
        return innerBounds;
    }


    public int getMaxDrawableDataPoints() {
        return (int) (isAnimating ? MAX_DRAWABLE_DATAPOINTS : 1000);
    }

    protected void drawHighlight(Layer layer) {
        if (endHighlight - beginHighlight == 0 || (beginHighlight < domainOrigin && endHighlight < domainOrigin) ||
                (beginHighlight > domainOrigin + currentDomain && endHighlight > domainOrigin + currentDomain)) {
            return;
        }

        // need plotBounds relative
        double ux = Math.max(0, domainToScreenX(beginHighlight, 0));
        double ex = Math.min(0 + plotBounds.width, domainToScreenX(endHighlight, 0));

        layer.setFillColor("rgba(20,255,255,255)");
        layer.setLayerAlpha(0.2f);
        layer.save();
        layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
        layer.fillRect(ux, 0, ex - ux, plotBounds.height);
        layer.restore();
    }


    protected void drawAxisPanel(Layer backingCanvas, AxisPanel axisPanel, Bounds bounds, boolean gridOnly) {
        axisPanel.drawAxisPanel(this, backingCanvas, bounds, gridOnly);
    }

    public double getDomainEnd() {
        return domainEnd;
    }

    public double getDomainStart() {
        return domainStart;
    }

    public void setHighlight(double begin, double end) {
        beginHighlight = begin;
        endHighlight = end;

    }

    public void setHighlight(int selStart, int selEnd) {

        int tmp = Math.min(selStart, selEnd);
        selEnd = Math.max(selStart, selEnd);
        selStart = tmp;
        beginHighlight = userXtoDomain(selStart, 0);
        endHighlight = userXtoDomain(selEnd, 0);
        redraw();
//        drawHighlight(hightLightLayer, hightLightLayer);
    }

    public boolean setHover(int x, int y) {
        int lastHover = hoverPoint;
        int lastHoverSeries = hoverSeries;
        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
            double domainX = userXtoDomain(x, i);
            double rangeY = userYtoRange(y, i);
            Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i, 10);
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

    public Nearest findNearestWithin(Nearest nearestResult, double domainX, double rangeY, int seriesNum, int within) {
        double cx = domainToScreenX(domainX, seriesNum);
        double cy = rangeToScreenY(rangeY, seriesNum);

        int where = Util.binarySearch(dataSets[seriesNum], domainX, currentMiplevels[seriesNum]);

        double x1 = domainToScreenX(dataSets[seriesNum].getX(where, currentMiplevels[seriesNum]), seriesNum);
        double y1 = rangeToScreenY(dataSets[seriesNum].getY(where, currentMiplevels[seriesNum]), seriesNum);
        double x2, y2;
        if (where + 1 < dataSets[seriesNum].getNumSamples(currentMiplevels[seriesNum])) {
            x2 = domainToScreenX(dataSets[seriesNum].getX(where + 1, currentMiplevels[seriesNum]), seriesNum);
            y2 = rangeToScreenY(dataSets[seriesNum].getY(where + 1, currentMiplevels[seriesNum]), seriesNum);
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

    private double dist(double x1, double y1, double cx, double cy) {
        return Math.sqrt((x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy));
    }

    protected double userYtoRange(int y, int seriesNum) {
        return dataSets[seriesNum].getRangeTop() - (y - plotBounds
                .y) / plotBounds.height * (dataSets[seriesNum].getRangeTop() - dataSets[seriesNum].getRangeBottom());

    }

    protected double userXtoDomain(double x, int seriesNum) {
        return domainOrigin + (x - plotBounds.x) / plotBounds.width * currentDomain;

    }

    public void setSelectionMode(boolean b) {
        this.selectionMode = b;
    }

    public boolean isSelectionModeEnabled() {
        return selectionMode;
    }

    public boolean setFocus(int x, int y) {
        int lastFocus = focusPoint;
        int lastFocusSeries = focusSeries;
        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
            double domainX = userXtoDomain(x, i);
            double rangeY = userYtoRange(y, i);
            Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i, 10);
            if (nearest.nearest > -1 && nearest.dist < nearDist) {
                nearNum = nearest.nearest;
                nearSer = nearest.series;
                nearDist = nearest.dist;
            }

        }
        if (nearNum >= 0) {
            _setFocusPoint(nearSer, nearNum + 1);

            if (lastFocusSeries != focusSeries || lastFocus != focusPoint) {

                redraw();
            }
            return true;
        } else {

            _setFocusPoint(-1, -1);
            if (lastFocusSeries != focusSeries || lastFocus != focusPoint) {
                redraw();
            }

        }
        return false;


    }

    public void maxZoomToFocus() {
        if (focusPoint != -1) {
            maxZoomToPoint(focusPoint, focusSeries);
        }
    }

    public void scrollPixels(int amt) {

        final double damt = (double) amt / plotBounds.width * currentDomain;

        domainOrigin += damt;
        redraw();
        view.fireScrollEvent(DefaultXYPlot.this, damt, 0, XYPlotListener.DRAGGED, false);


    }

    public void animateTo(final double destinationOrigin, final double destinationDomain, final int eventType) {
        animateTo(destinationOrigin, destinationDomain, eventType, null);
    }

    public void animateTo(final double destinationOrigin, final double destinationDomain, final int eventType,
                          final PortableTimerTask continuation) {

        if (animationTimer != null) {
            animationTimer.cancelTimer();
            animationTimer = null;
        }
        final double destDom =
                destinationDomain; //Fence domain, Math.min(destinationDomain, getDomainMax()-getDomainMin());
        double d = destinationOrigin;

// fence in origin
//        if(destinationOrigin < getDomainMin()) d = getDomainOrigin();
//        else if(destinationOrigin + destDom > getDomainMax()) d = getDomainMax() - destDom;
        final double destOrig = d;

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
                if (startTime == 0) startTime = t.getTime();
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
                    view.fireScrollEvent(DefaultXYPlot.this, startOrigin - domainOrigin, 0, eventType, false);
                    if (continuation != null) {
                        continuation.run(t);
                    }
                    lastFrame = true;
                    animationTimer.schedule(300);
                } else if (lastFrame) {
                    isAnimating = false;
                    redraw();
                }
            }
        });

        animationTimer.schedule(10);

    }


    public void prevZoom() {

        pushHistory();
        double nDomain = currentDomain * ZOOM_FACTOR;
        animateTo(getDomainCenter() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
    }

    public double getDomainCenter() {

        return domainOrigin + currentDomain / 2;
    }

    public void nextZoom() {
        pushHistory();
        double nDomain = currentDomain / ZOOM_FACTOR;
        animateTo(getDomainCenter() - nDomain / 2, nDomain, XYPlotListener.ZOOMED);
    }

    public boolean maxZoomTo(int x, int y) {

        int nearNum = -1;
        int nearSer = 0;
        double nearDist = Double.MAX_VALUE;

        for (int i = 0; i < dataSets.length; i++) {
            double domainX = userXtoDomain(x, i);
            double rangeY = userYtoRange(y, i);
            Nearest nearest = findNearestWithin(nearestSingleton, domainX, rangeY, i, 10);
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

    public void zoomToHighlight() {
        final double newOrigin = beginHighlight;

        double newdomain = endHighlight - beginHighlight;
        pushHistory();
        animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);


    }

    private void maxZoomToPoint(int point, int series) {
        pushHistory();

        XYDataset dataset = dataSets[series];
        point = Util.binarySearch(dataset, dataset.getX(point, currentMiplevels[series]), 0);

        final double newOrigin = dataset.getX(Math.max(0, point - 10));
        double newdomain = dataset.getX(Math.min(dataset.getNumSamples(), point + 10)) - newOrigin;

        animateTo(newOrigin, newdomain, XYPlotListener.ZOOMED);
    }

    public void maxZoomOut() {
        pushHistory();
        animateTo(domainMin, domainMax - domainMin, XYPlotListener.ZOOMED);
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


    public void scrollAndCenter(double domainX, PortableTimerTask continuation) {
        pushHistory();

        final double newOrigin = domainX - currentDomain / 2;
        animateTo(newOrigin, currentDomain, XYPlotListener.CENTERED, continuation);

    }

    protected void pushHistory() {
        Chronoscope.pushHistory();
    }


    public int getFocusSeries() {
        return focusSeries;
    }

    public int getFocusPoint() {
        return focusPoint;
    }

    public int getHoverSeries() {
        return hoverSeries;
    }

    public int getHoverPoint() {
        return hoverPoint;
    }

    public double getDataX(int serNum, int serPer) {
        return dataSets[serNum].getX(serPer, currentMiplevels[serNum]);
    }

    public double getDataY(int serNum, int serPer) {
        return dataSets[serNum].getY(serPer, currentMiplevels[serNum]);
    }

    public int getSeriesCount() {
        return dataSets.length;
    }

    public String getSeriesLabel(int i) {
        return dataSets[i].getRangeLabel();
    }

    /**
     * @param seriesNum
     * @return
     * @gwt.export getAxis
     */
    public RangeAxis getRangeAxis(int seriesNum) {
        return axes[seriesNum];
    }

    public ValueAxis getDomainAxis() {
        return domainAxis;
    }

    public XYRenderer getRenderer(int seriesNum) {
        return xyRenderers[seriesNum];
    }

    public void setRenderer(int seriesNum, XYRenderer r) {
        xyRenderers[seriesNum] = r;
    }


    public void setFocusPoint(int focusSeries, int focusPoint) {
        _setFocusPoint(focusSeries, focusPoint);
        redraw();
    }

    private void _setFocusPoint(int focusSeries, int focusPoint) {
        this.focusSeries = focusSeries;
        this.focusPoint = focusPoint;
        view.fireFocusEvent(this, focusSeries, focusPoint);
    }

    public int getCurrentMipLevel(int seriesNum) {
        return currentMiplevels[seriesNum];
    }

    public int getNearestVisiblePoint(double domainX, int series) {
        return Util.binarySearch(dataSets[series], domainX, currentMiplevels[series]);

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

    public boolean ensureVisible(int seriesNum, int pointNum, PortableTimerTask callback) {
        return ensureVisible(dataSets[seriesNum].getX(pointNum), dataSets[seriesNum].getY(pointNum), callback);
    }

    /**
     * @param domainX
     * @param rangeY
     * @param callback
     * @return
     * @gwt.export
     */
    public boolean ensureVisible(final double domainX, final double rangeY, PortableTimerTask callback) {
        view.ensureViewVisible();
        if (domainX <= domainOrigin || domainX >= domainX + currentDomain) {
            scrollAndCenter(domainX, callback);
            return true;
        }
        return false;

    }

    public Layer getOverviewLayer() {
        return overviewLayer;
    }

    public Bounds getOverviewBounds() {
        return overviewAxis.getBounds();
    }

    public OverviewAxis getOverviewAxis() {
        return overviewAxis;
    }

    public int getNumAnimationFrames() {
        return FRAMES;
    }

    /**
     * Any cached drawings of this axis are flushed and redrawn on next update
     *
     * @param axis
     */
    public void damageAxes(ValueAxis axis) {
        drewVertical = false;

    }

    public void setAxisForDataset(RangeAxis ra, int datasetNum) {
        axes[datasetNum] = ra;
    }

    public XYDataset getDataset(int i) {
        return dataSets[i];
    }

    public double getSelectionBegin() {
        return beginHighlight;
    }

    public double getSelectionEnd() {
        return endHighlight;
    }

    public void setLegendEnabled(boolean b) {
        showLegend = b;
    }

    public void setAnimating(boolean animating) {
        this.isAnimating = animating;
    }


    public void clearSelection() {
        selection = false;
        selStart = -1;
        selEnd = -1;

    }

    public void setDataset(int i, XYDataset d) {
        dataSets[i] = d;
    }

    public void setFocusPoint(int i, int point, int mip) {
        currentMiplevels[i] = mip;
        setFocusPoint(i, point);
    }


    public void openInfoWindow(final String html, final double domainX,

                               final double rangeY, final int seriesNum) {


        if (ensureVisible(domainX, rangeY, new PortableTimerTask() {

            public void run(PortableTimer timer) {
                view.openInfoWindow(html, chart.domainToWindowX(DefaultXYPlot.this, domainX, seriesNum),
                        chart.rangeToWindowY(DefaultXYPlot.this, rangeY, seriesNum) + 5);
            }
        })) {

        } else {
            view.openInfoWindow(html, chart.domainToWindowX(DefaultXYPlot.this, domainX, seriesNum),
                    chart.rangeToWindowY(DefaultXYPlot.this, rangeY, seriesNum) + 5);
        }

    }


    public String getHistoryToken() {
        return getChart().getChartId() + "(O" + getDomainOrigin() + ",D" + getCurrentDomain() + ")";

    }

    public boolean hasAxis(ValueAxis theAxis) {
        return topPanel.contains(theAxis) || domainPanel.contains(theAxis) || rangePanelLeft.contains(theAxis) ||
                rangePanelRight.contains(theAxis);
    }

    public Chart getChart() {
        return chart;
    }

    public void setCurrentDatasetLevel(int seriesNum, int mipLevel) {
        currentMiplevels[seriesNum] = mipLevel;
    }

    public int getNumDatasets() {
        return dataSets == null ? 0 : dataSets.length;
    }

    public XYPlot getPlotForAxis(ValueAxis theAxis) {
        return this;
    }


    public void setOverviewEnabled(boolean overviewEnabled) {
        this.overviewEnabled = overviewEnabled;
    }

}
