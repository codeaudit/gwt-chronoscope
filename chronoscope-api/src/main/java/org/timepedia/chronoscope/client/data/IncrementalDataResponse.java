package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Called by client code with incremental data.
 */
@ExportPackage("chronoscope")
@Export
public interface IncrementalDataResponse extends Exportable {
  void addData(JsArrayNumber domain, JsArray<JsArrayNumber> range);
}
