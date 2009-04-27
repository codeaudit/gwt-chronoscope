package org.timepedia.chronoscope.client;

import com.google.gwt.event.shared.EventHandler;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Created by IntelliJ IDEA. User: ray Date: Nov 12, 2008 Time: 11:56:42 PM To
* change this template use File | Settings | File Templates.
*/
@ExportPackage("chronoscope")
@Export
@ExportClosure
public interface InfoWindowClosedHandler extends EventHandler, Exportable {

  @Export
  void onInfoWindowClosed(InfoWindowEvent event);
}
