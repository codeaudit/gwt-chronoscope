package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.shared.AbstractEvent;
import com.google.gwt.libideas.event.shared.EventHandler;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * A handle to a Window opened via the View.openInfoWindow method.
 */
@ExportPackage("chronoscope")
public interface InfoWindow extends Exportable {


  @Export
  void close();

  @Export
  void setPosition(double x, double y);

  void addInfoWindowClosedHandler(InfoWindowClosedHandler handler);

  @Export
  void open();

  public interface InfoWindowClosedHandler extends EventHandler {

    void onInfoWindowClosed(InfoWindowEvent event);
  }

  public static class InfoWindowEvent
      extends AbstractEvent<InfoWindowClosedHandler> {

    public static Key<InfoWindowClosedHandler> KEY
        = new Key<InfoWindowClosedHandler>();

    protected void fireEvent(InfoWindowClosedHandler infoWindowClosedHandler) {
      infoWindowClosedHandler.onInfoWindowClosed(this);
    }

    protected Key getKey() {
      return KEY;
    }
  }
}
