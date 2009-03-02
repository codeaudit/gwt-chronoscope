package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

/**
 * Implement to handle PlotHoverEvent.
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface PlotHoverHandler extends EventHandler, Exportable 
{
  void onHover(PlotHoverEvent event);
}
