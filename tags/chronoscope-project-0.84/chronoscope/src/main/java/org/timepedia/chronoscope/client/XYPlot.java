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
 * An interface to be implemented by classes implementing XY plots of XY
 * Datasets. <p/> Conceptually, a Plot is a class responsible for maintaining a
 * collection of datasets, axes, and graph state, and mapping data-space values
 * to screen-space values suitable for rendering on a View. More specifically, a
 * plot converts data-space values to user-space values by delegating to an Axis
 * implementation. <p/> <p/> User-space values are in the interval [0,1]
 * independent of the screen space size of the plot. For example, a ValueAxis
 * with a visible range of 0.0 to 500.0, will map a data-value of 250.0 to a
 * user-space value of 0.5. A renderer like XYLineRenderer or XYBarRenderer will
 * convert user-space values ot screen-space values before rendering. When
 * Chronoscope has CategoryPlot support, then a CategoryAxis would map x-axis
 * values like "Groceries", "Gas", "Utilities" to appropriate user-space values
 * (say, 0, 0.5, 1.0) <p/> <p/> An important feature of Chronoscope is support
 * for large dataset scalability. This is achieved using datasets with
 * multiresolution representation. At coarser levels of detail, a dataset may be
 * decimated, interpolated, or filtered in a myriad of ways to compress its
 * size, while hopefully preserving as much signal as possible. <p/> A plot
 * maintains some of the following important values: <ul> <li>Domain origin
 * <li>Visible Domain <li>Focus point and series <li>Hover point and series
 * <li>Current highlight/selection </ul> <p/> As well as some stylistic
 * overrides: <ul> <li>Legend enabled/disabled <li>Domain axis rendering
 * enabled/disabled <li>Overview enabled/disabled <li>Selection mode on/off
 * </ul>
 */
public interface XYPlot extends Exportable {

  /**
   * Add an overlay to this plot
   */
  void addOverlay(Overlay overlay);

  /**
   * Animate the domainOrigin and currentDomain values interpolating to he
   * destination values.
   *
   * @param eventType hint to specify what kind of UI event this animation
   *                  corresponds to (ZOOM, SCROLL, ETC)
   */
  void animateTo(double destinationOrigin, double destinationDomain,
      int eventType);

  /**
   * Animate the domainOrigin and currentDomain values interpolating tot he
   * destination values. This version executes the continuation when the
   * animation finishes.
   *
   * @param eventType    hint to specify what kind of UI event this animation
   *                     corresponds to (ZOOM, SCROLL, ETC)
   * @param continuation executed when animation finishes
   */
  void animateTo(double destinationOrigin, double destinationDomain,
      int eventType, PortableTimerTask continuation);

  /**
   * Clears the current selection
   */
  void clearSelection();

  /**
   * Process a click on the Plot window given the screen space coordinates,
   * returns true if the click succeeded (e.g. it 'hit' something)
   */
  boolean click(int x, int y);

  /**
   * Tell plot to discard cache of the axes layer containing this axis and
   * redraw it on next update
   */
  void damageAxes(ValueAxis axis);

  /**
   * Convert a domain X value to a screen X value using the axis of the given
   * series number
   */
  double domainToScreenX(double domainX, int seriesNumber);

  /**
   * Returns the chart to which this Plot is embedded
   */
  Chart getChart();

  /**
   * Gets the currently visible domain for the plot
   */
  double getCurrentDomain();

  /**
   * Return the ith dataset
   */
  XYDataset getDataset(int i);

  /**
   * Retrieve X value for a given dataset and point at current visible
   * resolution level
   */
  double getDataX(int seriesNumber, int pointIndex);

  /**
   * Retrieve Y value for a given dataset and point at current visible
   * resolution level
   */
  double getDataY(int seriesNumber, int pointIndex);

  /**
   * Return the current domain axis
   */
  ValueAxis getDomainAxis();

  /**
   * Returns domain center. that is, domainOrigin + currentDomain/2
   */
  double getDomainCenter();

  /**
   * The maximum <b>visible</b> domain value over all datasets taking into
   * account multiresolution representations.
   */
  double getDomainEnd();

  /**
   * Maximum domain value over all datasets in the Plot
   */
  double getDomainMax();

  /**
   * Minimum domain value over all datasets in the Plot
   */
  double getDomainMin();

  /**
   * Returns the current domain origin
   */
  double getDomainOrigin();

  /**
   * The minimum <b>visible</b> domain value over all datasets taking into
   * account multiresolution representations. This value will differ from
   * getDomainMin() if the zoomed out view of the Plot forces the renderer to
   * user a coarser representation that may have different values. This can
   * happen if dataset values in higher levels use interpolation rather than
   * point sampling, for example.
   */
  double getDomainStart();

  /**
   * Return the current point which has the focus within the focused series
   */
  int getFocusPoint();

  /**
   * Return the current dataset index which has the focus
   */
  int getFocusSeries();

  /**
   * Returns a string representing the current state of the plot, used to
   * reconstruct the state of the plot at a later time.
   */
  String getHistoryToken();

  /**
   * Return the dataset index of the point which is currently in a hover state
   */
  int getHoverPoint();

  /**
   * Return the dataset index of the series whose point is set to a hover state
   */
  int getHoverSeries();

  /**
   * Return the Bounds of the plot area relative to the plot layer
   */
  Bounds getInnerPlotBounds();

  /**
   * A hint value suggesting the maximum number of datapoints this Plot should
   * try to render before performance may be hindered.
   */
  int getMaxDrawableDataPoints();

  /**
   * Given a domain value, and dataset number, return the nearest index within
   * the dataset to the given domain value
   */
  int getNearestVisiblePoint(double domainX, int seriesNum);

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
  Bounds getPlotBounds();

  /**
   * Find first plot that contains this axis (useful if using a multiplot)
   */
  XYPlot getPlotForAxis(ValueAxis theAxis);

  /**
   * Get the layer which represents the main plot area where points will be
   * renderered
   */
  Layer getPlotLayer();

  /**
   * @gwt.export getAxis
   */
  RangeAxis getRangeAxis(int axisNumber);

  /**
   * Return the renderer for a given dataset number
   */
  XYRenderer getRenderer(int seriesNum);

  /**
   * Get the domain value of the beginning of the current selection
   */
  double getSelectionBegin();

  /**
   * Get the domain value of the end of the current selection
   */
  double getSelectionEnd();

  /**
   * Returns true if this plot contains the axis
   */
  boolean hasAxis(ValueAxis theAxis);

  /**
   * Initialize or re-initialize the plot using the given view
   */
  void init(View view);

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
   * @param seriesNum the dataset these values come from (used to decide Axis
   *                  used)
   */
  void openInfoWindow(String html, double domainX, double rangeY,
      int seriesNum);

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
   * series number
   */
  double rangeToScreenY(double rangeY, int seriesNumber);

  /**
   * Calls update(), and manages optionally swaps double-buffered canvases
   */
  void redraw();

  /**
   * Reprocess all cached GSS properties (typically on stylesheet change)
   */
  void reloadStyles();

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
   * Set the resolution level for a given dataset
   */
  void setCurrentDatasetLevel(int seriesNum, int level);

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
   * Attempt to set the datapoint located at the given screen space coordinates
   * as the current focus point, returns true if succesful.
   */
  boolean setFocus(int x, int y);

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
   * should be rendered in
   */
  void setInitialBounds(Bounds initialBounds);

  /**
   * Enable or disable display of the legend
   */
  void setLegendEnabled(boolean enabled);

  /**
   * Enable or display mini-chart overview on x-axis
   */
  void setOverviewEnabled(boolean overviewEnabled);

  /**
   * Set the renderer for a given dataset number
   */
  void setRenderer(int seriesNum, XYRenderer renderer);

  /**
   * Set to true if you want drag operations on the plot to cause the selection
   * to change instead of the domain origin.
   */
  void setSelectionMode(boolean selectionEnabled);

  /**
   * Render the Plot into the encapsulating Chart's View
   */
  void update();

  /**
   * Causes chart to perform an animated zoom such that the current selection
   * becomes the currently visible domain.
   */
  void zoomToHighlight();
}