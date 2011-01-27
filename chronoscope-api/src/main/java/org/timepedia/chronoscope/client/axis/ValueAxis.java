package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.render.AxisPanel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * An ValueAxis is a class responsible for mapping points in data space to
 * points in screen space, as well as maintaining state related to drawing axis
 * ticks and labels. A given axis may be horizontal or vertical in orientation
 * depending on the {@link CompositeAxisPanel} it is placed into, and rendered on the
 * left/right or top/bottom depending on the AxisPanel position as well. 
 * <p> 
 * In GSS, a ValueAxis may be referred to using a CSS selector 'axis'. Each axis
 * is numbered, can have several CSS classes, depending on subtypes, like
 * "axis.range" or "axis.domain". See {@link AxisPanel} for more details.
 * 
 * @see org.timepedia.chronoscope.client.render.AxisPanel
 */
@ExportPackage("chronoscope")
public abstract class ValueAxis implements Exportable {

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
   * Returns the minimum and maximum data values on the axis.
   */
  public abstract Interval getExtrema();

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

  // TODO - export as axis.id rather than axis.getId() ?
  @Export("getId")
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
   * Returns the range of the axis
   */
  public final double getRange() {
    return getExtrema().length();
  }

  /**
   * Sets the short label representing this axis ($, m/s, etc)
   * @param axisId
   */
  @Export("setId")
  public void setAxisId(String axisId) {
    //TODO: this needs to update DefaultXYPlot's internal maps
    this.axisId = axisId;
  }

  /**
   * Sets the long descriptive label used for this axis (Billions of Dollars,
   * Barrels of Oil)
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Maps a given user position in the range [0,1] into the interval specified by
   * {@link #getExtrema()}.
   * 
   * @param userValue the user value to be mapped
   */
  public abstract double userToData(double userValue);
  
}
