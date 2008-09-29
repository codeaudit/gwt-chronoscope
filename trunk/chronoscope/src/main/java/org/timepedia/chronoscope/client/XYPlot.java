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
 * An interface to be implemented by classes implementing XY plots of
 * {@link XYDataset} objects.
 * <p>
 * Conceptually, an XYPlot is a class responsible for maintaining a collection
 * of datasets, axes, and graph state, and mapping data-space values to
 * screen-space values suitable for rendering on a {@link View}. More
 * specifically, a plot converts data-space values to user-space values by
 * delegating to an Axis implementation.
 * <p>
 * User-space values are in the interval [0,1] independent of the screen space
 * size of the plot. For example, a ValueAxis with a visible range of 0.0 to
 * 500.0, will map a data-value of 250.0 to a user-space value of 0.5. A
 * renderer like XYLineRenderer or XYBarRenderer will convert user-space values
 * to screen-space values before rendering. When Chronoscope has CategoryPlot
 * support, then a CategoryAxis would map x-axis values like "Groceries", "Gas",
 * "Utilities" to appropriate user-space values (say, 0, 0.5, 1.0)
 * <p>
 * An important feature of Chronoscope is support for large dataset scalability.
 * This is achieved using datasets with multiresolution representation. At
 * coarser levels of detail, a dataset may be decimated, interpolated, or
 * filtered in a myriad of ways to compress its size, while hopefully preserving
 * as much signal as possible.
 * <p>
 * A plot maintains some of the following important values:
 * <ul>
 * <li>Domain origin
 * <li>Visible Domain
 * <li>Focus point and series
 * <li>Hover point and series
 * <li>Current highlight/selection
 * </ul>
 * <p>
 * ... as well as some stylistic overrides:
 * <ul>
 * <li>Legend enabled/disabled
 * <li>Domain axis rendering enabled/disabled
 * <li>Overview enabled/disabled
 * <li>Selection mode on/off
 * </ul>
 */
public interface XYPlot extends Exportable {

  /**
   * Add an overlay to this plot.
   */
  void addOverlay(Overlay overlay);

  /**
   * Animate the domainOrigin and currentDomain values interpolating to he
   * destination values.
   * 
   * @param eventType hint to specify what kind of UI event this animation
   *          corresponds to (ZOOM, SCROLL, etc.)
   */
  void animateTo(double destinationOrigin, double destinationDomain,
      int eventType);

  /**
   * Animate the domainOrigin and currentDomain values interpolating to the
   * destination values. This version executes the continuation when the
   * animation finishes.
   * 
   * @param eventType hint to specify what kind of UI event this animation
   *          corresponds to (ZOOM, SCROLL, ETC)
   * @param continuation executed when animation finishes
   */
  void animateTo(double destinationOrigin, double destinationDomain,
      int eventType, PortableTimerTask continuation);

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
   * Returns the chart to which this Plot is embedded.
   */
  Chart getChart();

  /**
   * Return the active mip level for the given dataset index.
   */
  int getCurrentMipLevel(int datasetIndex);

  /**
   * Gets the currently visible domain for the plot.
   */
  double getCurrentDomain();

  /**
   * Return the ith dataset.
   */
  XYDataset getDataset(int i);

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
   * Return the current domain axis.
   */
  ValueAxis getDomainAxis();

  /**
   * Returns domain center. that is, domainOrigin + currentDomain/2
   */
  double getDomainCenter();

  /**
   * The maximum <b>visible</b> domain value over all datasets taking into
   * account multiresolution representations.  This value will differ from
   * {@link #getDomainMax()} if the zoomed out view of the Plot forces the 
   * renderer to use a coarser representation that may have different values. 
   * This can happen if dataset values in higher levels use interpolation rather 
   * than point sampling, for example.
   */
  double getVisibleDomainMax();

  /**
   * Maximum domain value over all datasets in the Plot.
   */
  double getDomainMax();

  /**
   * Minimum domain value over all datasets in the Plot.
   */
  double getDomainMin();

  /**
   * Returns the current domain origin.
   */
  double getDomainOrigin();

  /**
   * Returns the current focus point and series index within the focused series.
   * 
   */
  Focus getFocus();

  /**
   * Returns a string representing the current state of the plot, used to
   * reconstruct the state of the plot at a later time.
   */
  String getHistoryToken();

  /**
   * Returns an array of data point indices, which element k corresponds to the
   * data point being hovered on in dataset k. A value of -1 indicates that no
   * point in dataset [k] is currently being hovered. The length of the array is
   * equal to {@link #getNumDatasets()}.
   */
  int[] getHoverPoints();

  /**
   * Return the Bounds of the plot area relative to the plot layer
   */
  Bounds getInnerBounds();

  /**
   * A hint value suggesting the maximum number of datapoints that should be drawn 
   * in the view and maintain interactive framerates for this renderer
   */
  int getMaxDrawableDataPoints();

  /**
   * Given a domain value, and dataset index, return the nearest index within
   * the dataset to the given domain value
   */
  int getNearestVisiblePoint(double domainX, int datasetIndex);

  /**
   * Returns number of frames used during animateTo() calls
   */
  int getNumAnimationFrames();

  /**
   * Return the number of datasets in this plot
   */
  int getNumDatasets();

  /**
   * Return the current overview axis
   */
  OverviewAxis getOverviewAxis();

  /**
   * Returns the layer containing the overview
   */
  Layer getOverviewLayer();

  /**
   * Return the Bounds of the Plot relative to the View coordinate system
   */
  Bounds getBounds();

  /**
   * Find first plot that contains this axis (useful if using a multiplot)
   */
  XYPlot getPlotForAxis(ValueAxis theAxis);

  /**
   * Get the layer which represents the main plot area where points will be
   * rendered
   */
  Layer getPlotLayer();

  /**
   * @gwt.export getAxis
   */
  RangeAxis getRangeAxis(int datasetIndex);

  /**
   * Return number of unique Range axes in the plot
   */
  int getRangeAxisCount();

  /**
   * Return the renderer for a given dataset index
   */
  XYRenderer getRenderer(int datasetIndex);

  /**
   * Get the domain value of the beginning of the current selection
   */
  double getSelectionBegin();

  /**
   * Get the domain value of the end of the current selection
   */
  double getSelectionEnd();

  int getSeriesCount();

  String getSeriesLabel(int i);

  /**
   * Returns true if this plot contains the axis
   */
  boolean hasAxis(ValueAxis theAxis);

  /**
   * Initialize or re-initialize the plot using the given view
   */
  void init(View view);
  
  /**
   * Returns true if mini-chart overview on x-axis is enabled.
   */
  boolean isOverviewEnabled();
  
  /**
   * Is selection mode (dragging changes selection instead of panning the plot)
   * enabled?
   */
  boolean isSelectionModeEnabled();

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
   * positioned at its left edge upon the next call to {@link #redraw()}.
   */
  void moveTo(double domainX);

  /**
   * Advance the focused datapoint to the previous point
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
   *          used)
   */
  InfoWindow openInfoWindow(String html, double domainX, double rangeY,
      int datasetIndex);

  /**
   * Animated pan to the left by the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  void pageLeft(double pageSize);

  /**
   * Animated pan to the right the designated percentage (0.0, 1.0) of the
   * currently visible domain. For example, 0.5 will move the domain origin by
   * getCurrentDomain() * 0.5
   */
  void pageRight(double pageSize);

  /**
   * Advance the focused datapoint to the next point
   */
  void prevFocus();

  /**
   * Animated zoom-out of the currently visible domain by a fixed zoomfactor
   */
  void prevZoom();

  /**
   * Convert a range Y value to a screen Y value using the axis of the given
   * dataset index
   */
  double rangeToScreenY(double rangeY, int datasetIndex);

  double rangeToWindowY(double rangeY, int datasetIndex);

  /**
   * Calls update(), and manages optionally swaps double-buffered canvases
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
   * used to speed up framerate
   */
  void setAnimating(boolean animating);

  /**
   * Set the chart which is encapsulating this view
   */
  void setChart(Chart chart);

  /**
   * Set the active mip level for a given dataset
   */
  void setCurrentMipLevel(int datasetIndex, int level);

  /**
   * Sets the currently visible domain
   */
  void setCurrentDomain(double currentDomain);

  /**
   * Controls whether the domain axis (x-axis) is drawn or not.
   */
  void setDomainAxisVisible(boolean visible);

  /**
   * Sets the current domain origin
   */
  void setDomainOrigin(double domainOrigin);

  /**
   * Set the focus to be dataset element {point} for series {series}
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
   * Set the initial inset bounds (relative to the plot layer) that this plot
   * should be rendered in.
   */
  void setInitialBounds(Bounds initialBounds);

  /**
   * Enable or disable display of the legend.
   */
  void setLegendEnabled(boolean enabled);

  /**
   * Enable mini-chart overview on x-axis.
   */
  void setOverviewEnabled(boolean overviewEnabled);

  /**
   * Set the renderer for a given dataset index.
   */
  void setRenderer(int datasetIndex, XYRenderer renderer);

  /**
   * Set to true if you want drag operations on the plot to cause the selection
   * to change instead of the domain origin.
   */
  void setSelectionMode(boolean selectionEnabled);

  /**
   * Render the Plot into the encapsulating Chart's View.
   */
  void update();

  /**
   * Causes chart to perform an animated zoom such that the current selection
   * becomes the currently visible domain.
   */
  void zoomToHighlight();
}
