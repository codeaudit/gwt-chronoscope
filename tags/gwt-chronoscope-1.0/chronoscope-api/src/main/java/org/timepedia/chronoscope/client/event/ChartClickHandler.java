package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;

import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Implement to handle ChartClickEvent.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface ChartClickHandler extends EventHandler, Exportable {

  void onChartClick(ChartClickEvent event);
}