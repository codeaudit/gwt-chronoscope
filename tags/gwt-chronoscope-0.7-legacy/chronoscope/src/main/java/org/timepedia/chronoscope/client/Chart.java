package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.exporter.client.Exportable;

/**
 * The Chart class composes a Plot and a View. A plot is a platform independent model of all the components neccessarily
 * to render a dataset, and a View is a platform specific component (browser canvas, Java2d environment, Flash canvas)
 * that provides the operations neccessarily for interfacing with the graphics system.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class Chart implements Exportable {
    private XYPlot plot;
    private View view;
    private String id;


    public void init(View view, XYPlot plot) {
        this.view = view;
        initPlot(plot);
    }

    /**
     * For multiplots, this will be used to set the focus of keyboard/mouse events actions. For example, if you combine
     * two plots using a future SharedDomainXYPlot class, this method will do hit detection or delegate to a composition
     * class which does it, in order to determine which of the subplots should receive navigation commands
     *
     * @param x
     * @param y
     */
    public void setPlotFocus(int x, int y) {
        // future delegation, if plot instanceof HasSubplots
    }

    /**
     * When an animation is in progress, a lower resolution view of the dataset is used to speed up framerate
     *
     * @param animating
     */
    public void setAnimating(boolean animating) {
        plot.setAnimating(animating);
    }

    /**
     * Redraw will redraw the the chart, swapping front/back buffers if neccessarily in a double-buffered scenario.
     *
     * @gwt.export
     */
    public void redraw() {
        plot.redraw();
    }

    /**
     * Causes chart to perform an animated zoom such that the current selection becomes the currently visible domain.
     */
    public void zoomToHighlight() {
        plot.zoomToHighlight();
    }

    /**
     * Is the given screen coordinate inside the Plot area?
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isInsidePlot(int x, int y) {
        return plot.getPlotBounds().inside(x, y);
    }


    /**
     * Pan the current domain of the plot by the given number of screen pixels (positive or negative)
     *
     * @param pixels
     */
    public void scrollPixels(int pixels) {
        plot.scrollPixels(pixels);
    }

    /**
     * Attempt to set the datapoint at the screen space coordinates given to a hover state.
     *
     * @param x
     * @param y
     */
    public void setHover(int x, int y) {
        plot.setHover(x, y);
    }

    /**
     * Advance the focused datapoint to the previous point
     */
    public void prevFocus() {
        plot.prevFocus();
    }

    /**
     * Advance the focused datapoint to the next point
     */
    public void nextFocus() {
        plot.nextFocus();
    }

    /**
     * Animated pan to the left by the designated percentage (0.0, 1.0) of the currently visible domain. For example,
     * 0.5 will move the domain origin by getCurrentDomain() * 0.5
     *
     * @param amt
     */
    public void pageLeft(double amt) {
        plot.pageLeft(amt);
    }

    /**
     * Animated pan to the right the designated percentage (0.0, 1.0) of the currently visible domain. For example,
     * 0.5 will move the domain origin by getCurrentDomain() * 0.5
     *
     * @param amt
     */
    public void pageRight(double amt) {
        plot.pageRight(amt);
    }

    /**
     * Animated zoom to the currently focused point, such that it is located in the center of the destination
     * plot, and the width of the destination domain contains up to a maximum of plot.getMaxDrawablePoints()
     */
    public void maxZoomToFocus() {
        plot.maxZoomToFocus();
    }

    /**
     * Animated zoom-in of the currently visible domain by a fixed zoomfactor
     */
    public void nextZoom() {
        plot.nextZoom();
    }

    /**
     * Animated zoom-out of the currently visible domain by a fixed zoomfactor
     */
    public void prevZoom() {
        plot.prevZoom();
    }

    /**
     * Animated zoom out so that the entire domain of the dataset fits precisely in the Plot
     */
    public void maxZoomOut() {
        plot.maxZoomOut();
    }

    /**
     * Attempt to set the datapoint located at the given screen space coordinates as the current focus point, returns
     * true if succesful.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean setFocus(int x, int y) {
        return plot.setFocus(x, y);
    }

    /**
     * Process a click on the Plot window given the screen space coordinates, returns true if the click succeeded
     * (e.g. it 'hit' something)
     *
     * @param x
     * @param y
     * @return
     */
    public boolean click(int x, int y) {
        return plot.click(x, y);
    }

    /**
     * Animated zoom to the nearest datapoint at the given screen space coordinates. Functions like maxZoomToFocus()
     *
     * @param x
     * @param y
     * @return
     */
    public boolean maxZoomTo(int x, int y) {
        return plot.maxZoomTo(x, y);
    }

    /**
     * Returns a string representing the current state of the plot, used to reconstruct the state of the plot at a later
     * time.
     *
     * @return
     */
    public String getHistoryToken() {
        return plot.getHistoryToken();
    }


    /**
     * The current plot. This method is likely to change, as the number of plots expand, XYPlot will become a
     * subinterface of a more general 'Plot' interface
     *
     * @return
     * @gwt.export
     */
    public XYPlot getPlot() {
        return plot;
    }

    /**
     * The current view used for rendering.
     *
     * @return
     */
    public View getView() {
        return view;
    }

    /**
     * Find first plot that contains this axis (useful if using a multiplot)
     *
     * @param theAxis
     * @return
     */
    public XYPlot getPlotForAxis(ValueAxis theAxis) {
        return plot.getPlotForAxis(theAxis);
    }

    /**
     * Tell plot to discard cache of the axes layer containing this axis and redraw it on next update
     *
     * @param axis
     */
    public void damageAxes(ValueAxis axis) {
        plot.damageAxes(axis);
    }

    /**
     * Reprocess all style information and reinitialize the plot. useful for style-sheet changing.
     */
    public void reloadStyles() {
        plot.reloadStyles();
    }

    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    /**
     * Sets the internal plot instance to the given XYPlot, as well as initializes (or reinitialized) the plo by
     * invoking plot.init(view)
     *
     * @param plot
     */
    public void initPlot(XYPlot plot) {
        this.plot = plot;
        plot.setChart(this);
        plot.init(view);
        view.setChart(this);
    }

    /**
     * Sets the currently highlighted region based on starting and ending X screen coordinates
     *
     * @param screenX
     * @param endScreenX
     */
    public void setHighlight(int screenX, int endScreenX) {
        plot.setHighlight(screenX, endScreenX);
    }

    /**
     * Convert a domain X value to a screen location relative to the Chart (not Plot)
     *
     * @param plot
     * @param domainX
     * @param seriesNum
     * @return
     */
    public double domainToWindowX(XYPlot plot, double domainX, int seriesNum) {
        return plot.domainToScreenX(domainX, seriesNum) + plot.getPlotBounds().x;
    }

    /**
     * Convert a range Y value to a screen location relative to the Chart (not Plot)
     *
     * @param plot
     * @param rangeY
     * @param seriesNum
     * @return
     */
    public double rangeToWindowY(XYPlot plot, double rangeY, int seriesNum) {
        return plot.rangeToScreenY(rangeY, seriesNum) + plot.getPlotBounds().y;
    }


    /**
     * Sets a stable ID which is used to serialize chart state
     */
    public void setChartId(String id) {

        this.id = id;
    }

    /**
     * A stable ID used to serialize chart state
     *
     * @return
     */
    public String getChartId() {
        return id;
    }
}
