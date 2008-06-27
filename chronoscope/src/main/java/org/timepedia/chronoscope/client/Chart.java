package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 * The Chart class composes a Plot and a View. A plot is a platform independent
 * model of all the components neccessarily to render a dataset, and a View is a
 * platform specific component (browser canvas, Java2d environment, Flash
 * canvas) that provides the operations neccessarily for interfacing with the
 * graphics system.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class Chart implements Exportable {

  private XYPlot plot;

  private View view;

  private String id;

  /**
   * Process a click on the Plot window given the screen space coordinates,
   * returns true if the click succeeded (e.g. it 'hit' something)
   */
  public boolean click(int x, int y) {
    return plot.click(x, y);
  }

  /**
   * Tell plot to discard cache of the axes layer containing this axis and
   * redraw it on next update
   */
  public void damageAxes(ValueAxis axis) {
    plot.damageAxes(axis);
  }

  /**
   * Convert a domain X value to a screen location relative to the Chart (not
   * Plot)
   */
  public double domainToWindowX(XYPlot plot, double domainX, int seriesNum) {
    return plot.domainToScreenX(domainX, seriesNum) + plot.getPlotBounds().x;
  }

  /**
   * A stable ID used to serialize chart state
   */
  public String getChartId() {
    return id;
  }

  /**
   * Returns a string representing the current state of the plot, used to
   * reconstruct the state of the plot at a later time.
   */
  public String getHistoryToken() {
    return plot.getHistoryToken();
  }

  /**
   * The current plot. This method is likely to change, as the number of plots
   * expand, XYPlot will become a subinterface of a more general 'Plot'
   * interface
   *
   */
  @Export
  public XYPlot getPlot() {
    return plot;
  }

  /**
   * Find first plot that contains this axis (useful if using a multiplot)
   */
  public XYPlot getPlotForAxis(ValueAxis theAxis) {
    return plot.getPlotForAxis(theAxis);
  }

  /**
   * The current view used for rendering.
   */
  public View getView() {
    return view;
  }

  public void init(View view, XYPlot plot) {
    this.view = view;
    initPlot(plot);
  }

  /**
   * Sets the internal plot instance to the given XYPlot, as well as initializes
   * (or reinitialized) the plo by invoking plot.init(view)
   */
  public void initPlot(XYPlot plot) {
    this.plot = plot;
    plot.setChart(this);
    plot.init(view);
    view.setChart(this);
  }

  /**
   * Is the given screen coordinate inside the Plot area?
   */
  public boolean isInsidePlot(int x, int y) {
    return plot.getPlotBounds().inside(x, y);
  }

  /**
   * Animated zoom out so that the entire domain of the dataset fits precisely
   * in the Plot
   */
  public void maxZoomOut() {
    plot.maxZoomOut();
  }

  /**
   * Animated zoom to the nearest datapoint at the given screen space
   * coordinates. Functions like maxZoomToFocus()
   */
  public boolean maxZoomTo(int x, int y) {
    return plot.maxZoomTo(x, y);
  }

  /**
   * Animated zoom to the currently focused point, such that it is located in
   * the center of the destination plot, and the width of the destination domain
   * contains up to a maximum of plot.getMaxDrawablePoints()
   */
  public void maxZoomToFocus() {
    plot.maxZoomToFocus();
  }

  /**
   * Advance the focused datapoint to the next point
   */
  public void nextFocus() {
    plot.nextFocus();
  }

  /**
   * Animated zoom-in of the currently visible domain by a fixed zoomfactor
   */
  public void nextZoom() {
    plot.nextZoom();
  }

  /**
   * Animated pan to the left by the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  public void pageLeft(double amt) {
    plot.pageLeft(amt);
  }

  /**
   * Animated pan to the right the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  public void pageRight(double amt) {
    plot.pageRight(amt);
  }

  /**
   * Advance the focused datapoint to the previous point
   */
  public void prevFocus() {
    plot.prevFocus();
  }

  /**
   * Animated zoom-out of the currently visible domain by a fixed zoomfactor
   */
  public void prevZoom() {
    plot.prevZoom();
  }

  /**
   * Convert a range Y value to a screen location relative to the Chart (not
   * Plot)
   */
  public double rangeToWindowY(XYPlot plot, double rangeY, int seriesNum) {
    return plot.rangeToWindowY(rangeY, seriesNum);
  }

  /**
   * Redraw will redraw the the chart, swapping front/back buffers if
   * neccessarily in a double-buffered scenario.
   */
  @Export
  public void redraw() {
    plot.redraw();
  }

  /**
   * Reprocess all style information and reinitialize the plot. useful for
   * style-sheet changing.
   */
  public void reloadStyles() {
    plot.reloadStyles();
  }

  /**
   * Pan the current domain of the plot by the given number of screen pixels
   * (positive or negative)
   */
  public void scrollPixels(int pixels) {
    plot.scrollPixels(pixels);
  }

  /**
   * When an animation is in progress, a lower resolution view of the dataset is
   * used to speed up framerate
   */
  public void setAnimating(boolean animating) {
    plot.setAnimating(animating);
  }

  /**
   * Sets a stable ID which is used to serialize chart state
   */
  public void setChartId(String id) {

    this.id = id;
  }

  /**
   * Attempt to set the datapoint located at the given screen space coordinates
   * as the current focus point, returns true if succesful.
   */
  public boolean setFocus(int x, int y) {
    return plot.setFocusXY(x, y);
  }

  /**
   * Sets the currently highlighted region based on starting and ending X screen
   * coordinates
   */
  public void setHighlight(int screenX, int endScreenX) {
    plot.setHighlight(screenX, endScreenX);
  }

  /**
   * Attempt to set the datapoint at the screen space coordinates given to a
   * hover state.
   */
  public boolean setHover(int x, int y) {
    return plot.setHover(x, y);
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }

  /**
   * For multiplots, this will be used to set the focus of keyboard/mouse events
   * actions. For example, if you combine two plots using a future
   * SharedDomainXYPlot class, this method will do hit detection or delegate to
   * a composition class which does it, in order to determine which of the
   * subplots should receive navigation commands
   */
  public void setPlotFocus(int x, int y) {
    // future delegation, if plot instanceof HasSubplots
  }

  /**
   * Causes chart to perform an animated zoom such that the current selection
   * becomes the currently visible domain.
   */
  public void zoomToHighlight() {
    plot.zoomToHighlight();
  }

  public void setCursor(Cursor cursor) {
    view.setCursor(cursor);
  }
}
