package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;

import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Implement to handle PlotChangedEvent.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface PlotChangedHandler extends EventHandler, Exportable {
  void onChanged(PlotChangedEvent event);
}