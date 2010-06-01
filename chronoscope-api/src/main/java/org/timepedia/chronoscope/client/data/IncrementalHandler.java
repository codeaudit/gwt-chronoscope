package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Called when the chart needs additional data for a given range.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface IncrementalHandler extends Exportable {
  void onDataNeeded(Interval domain, Dataset dataset, IncrementalDataResponse response);
}
