package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Implement to handle PlotFocusEvent.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface PlotFocusHandler extends EventHandler, Exportable {
  void onFocus(PlotFocusEvent event);
}