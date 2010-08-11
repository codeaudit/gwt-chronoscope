package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

/**
 * Implement to handle PlotMovedEvent.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface PlotMovedHandler extends EventHandler, Exportable {
  void onMoved(PlotMovedEvent event);
}