package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

/**
 * The Chart class composes a {@link XYPlot} and a {@link View}. A plot is a 
 * platform-independent model of all the components necessary to render a 
 * dataset, and a view is a platform specific component (browser canvas, Java2d
 * environment, Flash canvas) that provides the operations necessary for 
 * interfacing with the graphics system.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
@ExportPackage("chronoscope")
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
   * A stable ID used to serialize chart state
   */
  @Export
  public String getChartId() {
    return id;
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
   * The current view used for rendering.
   */
  public View getView() {
    return view;
  }

  public void init() {
    ArgChecker.isNotNull(this.plot, "this.plot");
    ArgChecker.isNotNull(this.view, "this.view");
    view.setChart(this);
  }

  /**
   * Animated zoom out so that the entire domain of the dataset fits precisely
   * in the Plot
   */
  @Export
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
  @Export
  public void maxZoomToFocus() {
    plot.maxZoomToFocus();
  }

  /**
   * Advance the focused datapoint to the next point
   */
  @Export
  public void nextFocus() {
    plot.nextFocus();
  }

  /**
   * Animated zoom-in of the currently visible domain by a fixed zoomfactor
   */
  @Export
  public void nextZoom() {
    plot.nextZoom();
  }

  /**
   * Animated pan to the left by the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  @Export
  public void pageLeft(double amt) {
    plot.pageLeft(amt);
  }

  /**
   * Animated pan to the right the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  @Export
  public void pageRight(double amt) {
    plot.pageRight(amt);
  }

  /**
   * Advance the focused datapoint to the previous point
   */
  @Export
  public void prevFocus() {
    plot.prevFocus();
  }

  /**
   * Animated zoom-out of the currently visible domain by a fixed zoomfactor
   */
  @Export
  public void prevZoom() {
    plot.prevZoom();
  }

  /**
   * Redraw will redraw the the chart, swapping front/back buffers if
   * necessary in a double-buffered scenario.
   */
  @Export
  public void redraw() {
    plot.redraw();
  }

  /**
   * Reprocess all style information and reinitialize the plot. useful for
   * style-sheet changing.
   */
  @Export
  public void reloadStyles() {
    plot.reloadStyles();
  }

  /**
   * Pan the current domain of the plot by the given number of screen pixels
   * (positive or negative)
   */
  @Export
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
   * Attempt to set the datapoint at the screen space coordinates given to a
   * hover state.
   */
  public boolean setHover(int x, int y) {
    return plot.setHover(x, y);
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }

  public void setView(View view) {
    this.view = view;
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
  @Export
  public void zoomToHighlight() {
    plot.zoomToHighlight();
  }

  public void setCursor(Cursor cursor) {
    view.setCursor(cursor);
  }

  @Export
  public void setDomain(double start, double end) {
    plot.getDomain().setEndpoints(start, end);
  }
}
