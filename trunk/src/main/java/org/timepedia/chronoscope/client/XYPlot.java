package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.XYRenderer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Exportable;

/**
 * An interface to be implemented by classes implementing XY plots of XY Datasets.
 * <p/>
 * Conceptually, a Plot is a class responsible for maintaining a collection of datasets, axes, and graph state,
 * and mapping data-space values to screen-space values suitable for rendering on a View. More specifically,
 * a plot converts data-space values to user-space values by delegating to an Axis implementation.
 * <p/>
 * <p/>
 * User-space values are in the interval [0,1] independent of the screen space size of the plot. For example,
 * a ValueAxis with a visible range of 0.0 to 500.0, will map a data-value of 250.0 to a user-space value of 0.5.
 * A renderer like XYLineRenderer or XYBarRenderer will convert user-space values ot screen-space values before
 * rendering. When Chronoscope has CategoryPlot support, then a CategoryAxis would map x-axis values like
 * "Groceries", "Gas", "Utilities" to appropriate user-space values (say, 0, 0.5, 1.0)
 * <p/>
 * <p/>
 * An important feature of Chronoscope is support for large dataset scalability. This is achieved using datasets with
 * multiresolution representation. At coarser levels of detail, a dataset may be decimated, interpolated, or filtered
 * in a myriad of ways to compress its size, while hopefully preserving as much signal as possible.
 * <p/>
 * A plot maintains some of the following important values:
 * <ul>
 * <li>Domain origin
 * <li>Visible Domain
 * <li>Focus point and series
 * <li>Hover point and series
 * <li>Current highlight/selection
 * </ul>
 * <p/>
 * As well as some stylistic overrides:
 * <ul>
 * <li>Legend enabled/disabled
 * <li>Domain axis rendering enabled/disabled
 * <li>Overview enabled/disabled
 * <li>Selection mode on/off
 * </ul>
 */
public interface XYPlot extends Exportable {
    /**
     * Returns the current domain origin
     *
     * @return
     */
    double getDomainOrigin();

    /**
     * Sets the current domain origin
     *
     * @param domainOrigin
     */
    void setDomainOrigin(double domainOrigin);

    /**
     * Gets the currently visible domain for the plot
     *
     * @return
     */
    double getCurrentDomain();

    /**
     * Sets the currently visible domain
     *
     * @param currentDomain
     */
    void setCurrentDomain(double currentDomain);


    /**
     * Calls update(), and manages optionally swaps double-buffered canvases
     */
    void redraw();

    /**
     * Render the Plot into the encapsulating Chart's View
     */
    void update();


    /**
     * Advance the focused datapoint to the previous point
     */
    void nextFocus();

    /**
     * Advance the focused datapoint to the next point
     */
    void prevFocus();

    /**
     * Minimum domain value over all datasets in the Plot
     *
     * @return
     */
    double getDomainMin();

    /**
     * Maximum domain value over all datasets in the Plot
     *
     * @return
     */
    double getDomainMax();

    /**
     * Return the Bounds of the Plot relative to the View coordinate system
     *
     * @return
     */
    Bounds getPlotBounds();

    /**
     * Return the Bounds of the plot area relative to the plot layer
     *
     * @return
     */
    Bounds getInnerPlotBounds();

    /**
     * A hint value suggesting the maximum number of datapoints this Plot should try to render before performance may
     * be hindered.
     *
     * @return
     */
    int getMaxDrawableDataPoints();


    /**
     * The minimum <b>visible</b> domain value over all datasets taking into account multiresolution representations.
     * This value will differ from getDomainMin() if the zoomed out view of the Plot forces the renderer to user a
     * coarser representation that may have different values. This can happen if dataset values in higher levels use
     * interpolation rather than point sampling, for example.
     *
     * @return
     */
    double getDomainStart();

    /**
     * The maximum <b>visible</b> domain value over all datasets taking into account multiresolution representations.
     */
    double getDomainEnd();

    /**
     * Sets the currently visible highlight to the domain interval specified
     *
     * @param beginDomain
     * @param endDomain
     */
    void setHighlight(double beginDomain, double endDomain);

    /**
     * Sets the currently visible highlight to the given screen space coordinates
     *
     * @param beginScreenX
     * @param endScreenX
     */
    void setHighlight(int beginScreenX, int endScreenX);


    /**
     * Attempt to set the datapoint at the screen space coordinates given to a hover state.
     *
     * @param x
     * @param y
     */
    boolean setHover(int x, int y);

    /**
     * Set to true if you want drag operations on the plot to cause the selection to change instead of the
     * domain origin.
     *
     * @param selectionEnabled
     */
    void setSelectionMode(boolean selectionEnabled);

    /**
     * Is selection mode (dragging changes selection instead of panning the plot) enabled?
     *
     * @return
     */
    boolean isSelectionModeEnabled();

    /**
     * Attempt to set the datapoint located at the given screen space coordinates as the current focus point, returns
     * true if succesful.
     *
     * @param x
     * @param y
     * @return
     */
    boolean setFocus(int x, int y);

    /**
     * Animated zoom to the currently focused point, such that it is located in the center of the destination
     * plot, and the width of the destination domain contains up to a maximum of plot.getMaxDrawablePoints()
     */
    void maxZoomToFocus();

    /**
     * Pan the current domain of the plot by the given number of screen pixels (positive or negative)
     *
     * @param pixels
     */
    void scrollPixels(int pixels);

    /**
     * Animate the domainOrigin and currentDomain values interpolating to he destination values.
     *
     * @param destinationOrigin
     * @param destinationDomain
     * @param eventType         hint to specify what kind of UI event this animation corresponds to (ZOOM, SCROLL, ETC)
     */
    void animateTo(double destinationOrigin, double destinationDomain, int eventType);

    /**
     * Animate the domainOrigin and currentDomain values interpolating tot he destination values. This version executes
     * the continuation when the animation finishes.
     *
     * @param destinationOrigin
     * @param destinationDomain
     * @param eventType         hint to specify what kind of UI event this animation corresponds to (ZOOM, SCROLL, ETC)
     * @param continuation      executed when animation finishes
     */
    void animateTo(double destinationOrigin, double destinationDomain, int eventType, PortableTimerTask continuation);


    /**
     * Animated zoom-out of the currently visible domain by a fixed zoomfactor
     */
    void prevZoom();

    /**
     * Animated zoom-in of the currently visible domain by a fixed zoomfactor
     */
    void nextZoom();

    /**
     * Animated zoom to the nearest datapoint at the given screen space coordinates. Functions like maxZoomToFocus()
     *
     * @param x
     * @param y
     * @return
     */
    boolean maxZoomTo(int x, int y);

    /**
     * Causes chart to perform an animated zoom such that the current selection becomes the currently visible domain.
     */
    void zoomToHighlight();

    /**
     * Animated zoom out so that the entire domain of the dataset fits precisely in the Plot
     */
    void maxZoomOut();


    /**
     * Animated pan to the left by the designated percentage (0.0, 1.0) of the currently visible domain. For example,
     * 0.5 will move the domain origin by getCurrentDomain() * 0.5
     *
     * @param pageSize
     */
    void pageLeft(double pageSize);


    /**
     * Animated pan to the right the designated percentage (0.0, 1.0) of the currently visible domain. For example,
     * 0.5 will move the domain origin by getCurrentDomain() * 0.5
     *
     * @param pageSize
     */
    void pageRight(double pageSize);

    /**
     * Animated pan of the plot such that the given domain value is positioned in the center, the continuation is called
     * when finished.
     *
     * @param domainX
     * @param continuation
     */
    void scrollAndCenter(double domainX, PortableTimerTask continuation);

    /**
     * Return the current dataset index which has the focus
     *
     * @return
     */
    int getFocusSeries();

    /**
     * Return the current point which has the focus within the focused series
     *
     * @return
     */
    int getFocusPoint();

    /**
     * Return the dataset index of the series whose point is set to a hover state
     *
     * @return
     */
    int getHoverSeries();

    /**
     * Return the dataset index of the point which is currently in a hover state
     *
     * @return
     */
    int getHoverPoint();

    /**
     * Return the current domain axis
     *
     * @return
     */
    ValueAxis getDomainAxis();

    /**
     * Process a click on the Plot window given the screen space coordinates, returns true if the click succeeded
     * (e.g. it 'hit' something)
     *
     * @param x
     * @param y
     * @return
     */
    boolean click(int x, int y);

    /**
     * Return the current overview axis
     *
     * @return
     */
    OverviewAxis getOverviewAxis();


    /**
     * Tell plot to discard cache of the axes layer containing this axis and redraw it on next update
     *
     * @param axis
     */
    void damageAxes(ValueAxis axis);

    /**
     * Get the domain value of the beginning of the current selection
     *
     * @return
     */
    double getSelectionBegin();

    /**
     * Get the domain value of the end of the current selection
     *
     * @return
     */
    double getSelectionEnd();


    /**
     * Enable or disable display of the legend
     *
     * @param enabled
     */
    void setLegendEnabled(boolean enabled);

    /**
     * When an animation is in progress, a lower resolution view of the dataset is used to speed up framerate
     *
     * @param animating
     */
    void setAnimating(boolean animating);

    /**
     * Clears the current selection
     */
    void clearSelection();

    /**
     * Returns a string representing the current state of the plot, used to reconstruct the state of the plot at a later
     * time.
     *
     * @return
     */
    String getHistoryToken();

    /**
     * Returns true if this plot contains the axis
     *
     * @param theAxis
     * @return
     */
    boolean hasAxis(ValueAxis theAxis);

    /**
     * Returns the chart to which this Plot is embedded
     *
     * @return
     */
    Chart getChart();

    /**
     * Find first plot that contains this axis (useful if using a multiplot)
     *
     * @param theAxis
     * @return
     */
    XYPlot getPlotForAxis(ValueAxis theAxis);

    /**
     * Returns the layer containing the overview
     *
     * @return
     */
    Layer getOverviewLayer();

    /**
     * Initialize or re-initialize the plot using the given view
     *
     * @param view
     */
    void init(View view);

    /**
     * Set the initial inset bounds (relative to the plot layer) that this plot should be rendered in
     *
     * @param initialBounds
     */
    void setInitialBounds(Bounds initialBounds);

    /**
     * Set the chart which is encapsulating this view
     *
     * @param chart
     */
    void setChart(Chart chart);

    /**
     * Get the layer which represents the main plot area where points will be renderered
     *
     * @return
     */
    Layer getPlotLayer();

    /**
     * Returns number of frames used during animateTo() calls
     *
     * @return
     */
    int getNumAnimationFrames();

    /**
     * Returns domain center. that is, domainOrigin + currentDomain/2
     *
     * @return
     */
    double getDomainCenter();

    /**
     * Controls whether the domain axis (x-axis) is drawn or not.
     *
     * @param visible
     */
    void setDomainAxisVisible(boolean visible);

    /**
     * Add an overlay to this plot
     */
    void addOverlay(Overlay overlay);

    /**
     * @param axisNumber
     * @return
     * @gwt.export getAxis
     */
    RangeAxis getRangeAxis(int axisNumber);

    /**
     * Reprocess all cached GSS properties (typically on stylesheet change)
     */
    void reloadStyles();

    /**
     * Given a domain value, and dataset number, return the nearest index within the dataset to the given domain
     * value
     *
     * @param domainX
     * @param seriesNum
     * @return
     */
    int getNearestVisiblePoint(double domainX, int seriesNum);

    /**
     * Retrieve X value for a given dataset and point at current visible resolution level
     *
     * @param seriesNumber
     * @param pointIndex
     * @return
     */
    double getDataX(int seriesNumber, int pointIndex);

    /**
     * Retrieve Y value for a given dataset and point at current visible resolution level
     *
     * @param seriesNumber
     * @param pointIndex
     * @return
     */
    double getDataY(int seriesNumber, int pointIndex);

    /**
     * Convert a domain X value to a screen X value using the axis of the given series number
     *
     * @param domainX
     * @param seriesNumber
     * @return
     */
    double domainToScreenX(double domainX, int seriesNumber);

    /**
     * Convert a range Y value to a screen Y value using the axis of the given series number
     *
     * @param rangeY
     * @param seriesNumber
     * @return
     */
    double rangeToScreenY(double rangeY, int seriesNumber);

    /**
     * Return the renderer for a given dataset number
     *
     * @param seriesNum
     * @return
     */
    XYRenderer getRenderer(int seriesNum);

    /**
     * Set the resolution level for a given dataset
     *
     * @param seriesNum
     * @param level
     */
    void setCurrentDatasetLevel(int seriesNum, int level);

    /**
     * Return the number of datasets in this plot
     *
     * @return
     */
    int getNumDatasets();

    /**
     * Return the ith dataset
     *
     * @param i
     * @return
     */
    XYDataset getDataset(int i);

    /**
     * Enable or display mini-chart overview on x-axis
     *
     * @param overviewEnabled
     */
    void setOverviewEnabled(boolean overviewEnabled);


}
