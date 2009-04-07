package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 *
 */  
@ExportPackage("chronoscope")
@ExportClosure
public interface OverlayChangeHandler extends EventHandler {

  void onOverlayChanged(OverlayChangeEvent event);
}
