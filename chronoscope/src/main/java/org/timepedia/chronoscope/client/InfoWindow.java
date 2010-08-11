package org.timepedia.chronoscope.client;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

/**
 * A handle to a Window opened via the View.openInfoWindow method.
 */
@ExportPackage("chronoscope")
public interface InfoWindow extends Exportable {


  @Export
  void close();

  @Export
  void setPosition(double x, double y);

  @Export("addCloseHandler")
  void addInfoWindowClosedHandler(InfoWindowClosedHandler handler);

  @Export
  void open();

}
