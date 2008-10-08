package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.AxisRenderer;

/**
 * An ValueAxis is a class responsible for mapping points in data space to
 * points in screen space, as well as maintaining state related to drawing axis
 * ticks and labels. A given axis may be horizontal or vertical in orientation
 * depending on the {@link AxisPanel} it is placed into, and rendered on the
 * left/right or top/bottom depending on the AxisPanel position as well. 
 * <p> 
 * In GSS, an ValueAxis may be referred to using a CSS selector 'axis'. Each axis
 * is numbered, can have several CSS classes, depending on subtypes, like
 * "axis.range" or "axis.domain". See {@link AxisRenderer} for more details.
 * 
 * @see org.timepedia.chronoscope.client.render.AxisRenderer
 */
public abstract class ValueAxis {

  protected AxisPanel axisPanel;

  private String label;

  private String unitLabel;

  /**
   * Subclasses must call this constructor.
   *  
   * @param label - See {@link #getLabel()}
   * @param unitLabel - A short label representing the units of this 
   *      axis ("m/s", "$", etc.).
   */
  protected ValueAxis(String label, String unitLabel) {
    this.label = label;
    this.unitLabel = unitLabel;
  }

  /**
   * Maps a given dataValue in the interval [rangeLow, rangeHigh] to a a user
   * position in the range [0,1]
   * 
   * @param dataValue the value to be mapped
   */
  public abstract double dataToUser(double dataValue);

  /**
   * Draws the axis into the given layer, within the specified axisBounds, as
   * well as drawing grid-lines on the given DefaultXYPlot.
   * 
   * @param plot the plot to draw the gridlines into
   * @param layer the layer to render the axis on
   * @param axisBounds the bounds within the layer into which the axis should be
   *          drawn
   * @param gridOnly if true, only render gridlines into the plots, render
   *          nothing else
   */
  public abstract void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly);

  /**
   * Gets the short label representing the units of this axis (m/s, $, etc)
   */
  public String getAxisId() {
    return unitLabel;
  }

  public AxisPanel getAxisPanel() {
    return axisPanel;
  }

  /**
   * The height in pixels this axis will consume when rendered (including
   * padding, margins, etc)
   */
  public abstract double getHeight();

  /**
   * Gets the long descriptive label used for this axis ("Billions of Dollars",
   * "Barrels of Oil")
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the smallest range displayable on this axis, used to prevent
   * zooming too far.
   */
  public double getMinimumTickSize() {
    return Double.MIN_VALUE;
  }

  /**
   * Returns the range of the axis
   */
  public double getRange() {
    return getRangeHigh() - getRangeLow();
  }

  /**
   * Returns the maximum data value on the axis
   */
  public abstract double getRangeHigh();

  /**
   * Returns the minimum data value on the axis
   */
  public abstract double getRangeLow();

  /**
   * The width in pixels this axis will consume when rendered (including
   * padding, margins, etc)
   */
  public abstract double getWidth();

  /**
   * Called after an axis is attached to an axis panel
   */
  public abstract void init();

  /**
   * Sets the long descriptive label used for this axis (Billions of Dollars,
   * Barrels of Oil)
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Maps a given user position in the range [0,1] into the interval [rangeLow,
   * rangeHigh], where rangeLow = {@link #getRangeLow()} and 
   * rangeHigh = {@link #getRangeHigh()}.
   * 
   * @param userValue the user value to be mapped
   */
  public double userToData(double userValue) {
    return userToData(getRangeLow(), getRangeHigh(), userValue);
  }
  
  /**
   * Maps a given user position in the range [0,1] into the interval [rangeLow,
   * rangeHigh].
   * 
   * @param rangeLow the minimum data value on the axis
   * @param rangeHigh the maximum data value on the axis
   * @param userValue the user value to be mapped
   */
  protected double userToData(double rangeLow, double rangeHigh, double userValue) {
    return rangeLow + (userValue * (rangeHigh - rangeLow));
  }

  protected abstract void layout();
  
}
