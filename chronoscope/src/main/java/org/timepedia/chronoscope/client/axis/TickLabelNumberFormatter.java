package org.timepedia.chronoscope.client.axis;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Implement this class to override the look of RangeAxis tick labels.
 * @gwt.exportPackage chronoscope
 * @gwt.exportClosure
*/
@ExportPackage("chronoscope")
@ExportClosure
public interface TickLabelNumberFormatter {

  public String format(double value);
}
