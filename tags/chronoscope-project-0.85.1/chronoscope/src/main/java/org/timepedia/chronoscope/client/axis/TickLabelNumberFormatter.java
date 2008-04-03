package org.timepedia.chronoscope.client.axis;

/**
 * Implement this class to override the look of RangeAxis tick labels.
 * @gwt.exportPackage chronoscope
 * @gwt.exportClosure
*/
public interface TickLabelNumberFormatter {

  public String format(double value);
}
