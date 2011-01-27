package org.timepedia.chronoscope.client.axis;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Implement this class to override the look of RangeAxis tick labels.
*/
@ExportPackage("chronoscope")
@ExportClosure
public interface TickLabelNumberFormatter {

  public String format(double value);

  public String getFormat();
}
