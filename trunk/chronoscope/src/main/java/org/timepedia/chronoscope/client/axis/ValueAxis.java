package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.render.AxisPanel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;

/**
 * An ValueAxis is a class responsible for mapping points in data space to
 * points in screen space, as well as maintaining state related to drawing axis
 * ticks and labels. A given axis may be horizontal or vertical in orientation
 * depending on the {@link CompositeAxisPanel} it is placed into, and rendered on the
 * left/right or top/bottom depending on the AxisPanel position as well. 
 * <p> 
 * In GSS, an ValueAxis may be referred to using a CSS selector 'axis'. Each axis
 * is numbered, can have several CSS classes, depending on subtypes, like
 * "axis.range" or "axis.domain". See {@link AxisPanel} for more details.
 * 
 * @see org.timepedia.chronoscope.client.render.AxisPanel
 */
public abstract class ValueAxis {

  private String label;

  private String axisId;

  /**
   * Subclasses must call this constructor.
   *  
   * @param label - See {@link #getLabel()}
   * @param axisId - A unique identifier representing the units of this 
   *      axis ("m/s", "$", etc.).
   */
  protected ValueAxis(String label, String axisId) {
    this.label = label;
    this.axisId = axisId;
  }

  /**
   * Maps a given dataValue in the interval [rangeLow, rangeHigh] to a a user
   * position in the range [0,1]
   * 
   * @param dataValue the value to be mapped
   */
  public abstract double dataToUser(double dataValue);

  /**
   * Gets the short label representing the units of this axis (m/s, $, etc)
   */
  public String getAxisId() {
    return axisId;
  }

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

}
