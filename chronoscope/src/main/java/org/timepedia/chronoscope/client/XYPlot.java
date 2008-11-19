package org.timepedia.chronoscope.client;

import com.google.gwt.gen2.event.shared.HandlerRegistration;

import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.event.PlotFocusHandler;
import org.timepedia.chronoscope.client.event.PlotHoverHandler;
import org.timepedia.chronoscope.client.event.PlotMovedEvent;
import org.timepedia.chronoscope.client.event.PlotMovedHandler;
import org.timepedia.chronoscope.client.render.AxisPanel;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.DatasetRendererMap;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Exportable;

/**
 * An interface to be implemented by classes implementing XY plots of {@link
 * Dataset} objects. <p> Conceptually, an XYPlot is a class responsible for
 * maintaining a collection of datasets, axes, and graph state, and mapping
 * data-space values to screen-space values suitable for rendering on a {@link
 * View}. More specifically, a plot converts data-space values to user-space
 * values by delegating to an Axis implementation. <p> User-space values are in
 * the interval [0,1] independent of the screen space size of the plot. For
 * example, a ValueAxis with a visible range of 0.0 to 500.0, will map a
 * data-value of 250.0 to a user-space value of 0.5. A renderer like
 * XYLineRenderer or XYBarRenderer will convert user-space values to
 * screen-space values before rendering. When Chronoscope has CategoryPlot
 * support, then a CategoryAxis would map x-axis values like "Groceries", "Gas",
 * "Utilities" to appropriate user-space values (say, 0, 0.5, 1.0) <p> An
 * important feature of Chronoscope is support for large dataset scalability.
 * This is achieved using datasets with multiresolution representation. At
 * coarser levels of detail, a dataset may be decimated, interpolated, or
 * filtered in a myriad of ways to compress its size, while hopefully preserving
 * as much signal as possible. <p> A plot maintains some of the following
 * important values: <ul> <li>Domain origin <li>Visible Domain <li>Focus point
 * and dataset <li>Hover point and dataset <li>Current highlight/selection </ul>
 * <p> ... as well as some stylistic overrides: <ul> <li>Legend enabled/disabled
 * <li>Domain axis rendering enabled/disabled <li>Overview enabled/disabled
 * <li>Selection mode on/off </ul>
 */
public interface XYPlot<T extends Tuple2D> extends Exportable {

  /**
   * Add an overlay to this plot.
   */
  void addOverlay(Overlay overlay);

  /**
   * Add a callback for PlotFocusEvents.
   */
  HandlerRegistration addPlotFocusHandler(PlotFocusHandler handler);

  /**
   * Add a callback for PlotHoverEvents.
   */
  HandlerRegistration addPlotHoverHandler(PlotHoverHandler handler);

  /**
   * Add a callback for PlotMovedEvents.
   */
  HandlerRegistration addPlotMovedHandler(PlotMovedHandler handler);

  /**
   * Animate the domainOrigin and currentDomain values interpolating to the
   * destination values.
   *
   * @param eventType hint to specify what kind of UI event this animation
   *                  corresponds to (ZOOM, SCROLL, etc.)
   */
  void animateTo(double destDomainOrigin, double destCurrentDomain,
      PlotMovedEvent.MoveType eventType);

  /**
   * Animate the domainOrgigin and currentDomain values interpolating to the
   * destination values. This version executes the continuation when the
   * animation finishes.
   *
   * @param eventType    hint to specify what kind of UI event this animation
   *                     corresponds to (ZOOM, SCROLL, ETC)
   * @param continuation executed when animation finishes
   */
  void animateTo(double destDomainOrigin, double destCurrentDomain,
      PlotMovedEvent.MoveType eventType, PortableTimerTask continuation);

  /**
   * Process a click on the Plot window given the screen space coordinates,
   * returns true if the click succeeded (e.g. it 'hit' something)
   */
  boolean click(int x, int y);

  /**
   * Tell plot to discard cache of the axes layer containing this axis and
   * redraw it on next update.
   */
  void damageAxes(ValueAxis axis);

  /**
   * Convert a domain X value to a screen X value using the axis of the given
   * dataset index.
   */
  double domainToScreenX(double domainX, int datasetIndex);

  /**
   * Convert a domain X value to a window X value using the axis of the given
   * dataset index.
   */
  double domainToWindowX(double domainX, int datasetIndex);

  /**
   * Return the Bounds of the Plot relative to the View coordinate system
   */
  Bounds getBounds();

  /**
   * Returns the chart to which this Plot is embedded.
   */
  Chart getChart();

  /**
   * Return the active mip level for the given dataset index.
   */
  int getCurrentMipLevel(int datasetIndex);

  /**
   * Return the renderer for a given dataset index
   */
  DatasetRenderer<T> getDatasetRenderer(int datasetIndex);

  /**
   * Returns the datasets associated with this plot.
   */
  Datasets<T> getDatasets();

  /**
   * Retrieve X value for a given dataset and point at current visible
   * resolution level.
   */
  double getDataX(int datasetIndex, int pointIndex);

  /**
   * Retrieve Y value for a given dataset and point at current visible
   * resolution level.
   */
  double getDataY(int datasetIndex, int pointIndex);

  /**
   * Returns a line segment representing the portion of the dataset domain
   * that's currently visible within this plot.
   */
  Interval getDomain();

  /**
   * Return the current domain axis.
   */
  ValueAxis getDomainAxis();

  /**
   * Return renderer associated with X-axis.
   */
  AxisPanel getDomainAxisRenderer();

  /**
   * Returns the current focus point and dataset index within the focused
   * dataset.
   */
  Focus getFocus();

  /**
   * Returns a string representing the current state of the plot, used to
   * reconstruct the state of the plot at a later time.
   */
  String getHistoryToken();

  /**
   * Returns the layer onto which the hover points are rendered.
   */
  Layer getHoverLayer();

  /**
   * Returns an array of data point indices, which element k corresponds to the
   * data point being hovered on in dataset k. A value of -1 indicates that no
   * point in dataset [k] is currently being hovered. The length of the array is
   * equal to the number of datasets in {@link #getdatasets
   */
  int[] getHoverPoints();

  /**
   * Return the Bounds of the plot area relative to the plot layer
   */
  Bounds getInnerBounds();

  /**
   * A hint value suggesting the maximum number of datapoints that should be
   * drawn in the view and maintain interactive frame rates for this renderer
   */
  int getMaxDrawableDataPoints();

  /**
   * Given a domain value, and dataset index, return the nearest index within
   * the dataset to the given domain value
   */
  int getNearestVisiblePoint(double domainX, int datasetIndex);

  /**
   * Return the current overview axis panel.
   */
  OverviewAxisPanel getOverviewAxisPanel();

  /**
   * Returns the layer onto which the overview axis is rendered.
   */
  Layer getOverviewLayer();

  /**
   * Returns the layer onto which the main plot area is rendered.
   */
  Layer getPlotLayer();

  /**
   * @gwt.export getAxis
   */
  RangeAxis getRangeAxis(int datasetIndex);

  /**
   * Get the domain value of the beginning of the current selection
   */
  double getSelectionBegin();

  /**
   * Get the domain value of the end of the current selection
   */
  double getSelectionEnd();

  /**
   * The maximum <b>visible</b> domain value over all datasets taking into
   * account multiresolution representations.  This value will differ from
   * {@link Datasets#getMaxDomain()} if the zoomed out view of the Plot forces
   * the renderer to use a coarser representation that may have different
   * values. This can happen if dataset values in higher levels use
   * interpolation rather than point sampling, for example.
   */
  double getVisibleDomainMax();

  /**
   * Initialize or re-initialize the plot using the given view
   */
  void init(View view);

  /**
   * Returns true if mini-chart overview on x-axis is enabled.
   */
  boolean isOverviewEnabled();

  /**
   * Animated zoom out so that the entire domain of the dataset fits precisely
   * in the Plot
   */
  void maxZoomOut();

  /**
   * Animated zoom to the nearest datapoint at the given screen space
   * coordinates. Functions like maxZoomToFocus()
   */
  boolean maxZoomTo(int x, int y);

  /**
   * Animated zoom to the currently focused point, such that it is located in
   * the center of the destination plot, and the width of the destination domain
   * contains up to a maximum of plot.getMaxDrawablePoints()
   */
  void maxZoomToFocus();

  /**
   * Repositions the plot's viewport so that the specified domainX value will be
   * positioned at the viewport's left edge.  {@link #redraw()} is called at the
   * end of this method.
   */
  void moveTo(double domainX);

  /**
   * Advance the focused data point to the next data point.
   */
  void nextFocus();

  /**
   * Animated zoom-in of the currently visible domain by a fixed zoomfactor
   */
  void nextZoom();

  /**
   * Open an info window at the specified coordinates in data space
   *
   * @param datasetIndex the dataset these values come from (used to decide Axis
   *                     used)
   */
  InfoWindow openInfoWindow(String html, double domainX, double rangeY,
      int datasetIndex);

  /**
   * Animated pan to the left the designated percentage of this plot's visible
   * domain. For example, 0.5 will move the domain origin by visibleDomain *
   * 0.5.
   *
   * @param pageSize - A value in the range (0.0, 1.0)
   */
  void pageLeft(double pageSize);

  /**
   * Animated pan to the right the designated percentage of this plot's visible
   * domain. For example, 0.5 will move the domain origin by visibleDomain *
   * 0.5.
   *
   * @param pageSize - A value in the range (0.0, 1.0)
   */
  void pageRight(double pageSize);

  /**
   * Advance the focused data point to the previous data point.
   */
  void prevFocus();

  /**
   * Animated zoom-out of the currently visible domain by a fixed zoomfactor
   */
  void prevZoom();

  /**
   * Convert a range Y value to a screen Y value using the axis of the given
   * dataset index.
   */
  double rangeToScreenY(double rangeY, int datasetIndex);

  /**
   * Convert a range Y value to a window Y value using the axis of the given
   * dataset index.
   */
  double rangeToWindowY(double rangeY, int datasetIndex);

  /**
   * Redraws any part of the plot that has changed since the last invocation of
   * this method.
   */
  void redraw();

  /**
   * Reprocess all cached GSS properties (typically on stylesheet change)
   */
  void reloadStyles();

  /**
   * Remove an overlay from the Plot.
   */
  void removeOverlay(Overlay overlay);

  /**
   * Animated pan of the plot such that the given domain value is positioned in
   * the center, the continuation is called when finished.
   */
  void scrollAndCenter(double domainX, PortableTimerTask continuation);

  /**
   * Pan the current domain of the plot by the given number of screen pixels
   * (positive or negative)
   */
  void scrollPixels(int pixels);

  /**
   * When an animation is in progress, a lower resolution view of the dataset is
   * used to speed up frame rate
   */
  void setAnimating(boolean animating);

  /**
   * Set the active mip level for a given dataset
   */
  void setCurrentMipLevel(int datasetIndex, int level);

  /**
   * Associates a {@link DatasetRenderer} with a {@link Dataset}.
   *
   * @param datasetIndex the index of the dataset to be rendered
   * @param renderer the renderer responsible for drawing the dataset
   */
  void setDatasetRenderer(int datasetIndex, DatasetRenderer<T> renderer);
  
  /**
   * Sets the map that determines which {@link DatasetRenderer} to use 
   * for a given {@link Dataset} object.
   */
  void setDatasetRendererMap(DatasetRendererMap datasetRendererMap);
  
  /**
   * Overrides the default X-axis renderer (Date formatted labels)
   */
  void setDomainAxisRenderer(AxisPanel domainAxis);

  /**
   * Sets the specified datapoint reference as the focused point in this plot.
   */
  void setFocus(Focus focus);

  /**
   * Attempt to set the datapoint located at the given screen space coordinates
   * as the current focus point, returns true if succesful.
   */
  boolean setFocusXY(int x, int y);

  /**
   * Sets the currently visible highlight to the domain interval specified
   */
  void setHighlight(double beginDomain, double endDomain);

  /**
   * Sets the currently visible highlight to the given screen space coordinates
   */
  void setHighlight(int beginScreenX, int endScreenX);

  /**
   * Attempt to set the datapoint at the screen space coordinates given to a
   * hover state.
   */
  boolean setHover(int x, int y);

  /**
   * Enable or disable display of the legend.
   */
  void setLegendEnabled(boolean enabled);

  /**
   * Enable mini-chart overview on x-axis.
   */
  void setOverviewEnabled(boolean overviewEnabled);

  /**
   * Causes chart to perform an animated zoom such that the current selection
   * becomes the currently visible domain.
   */
  void zoomToHighlight();

  
}
