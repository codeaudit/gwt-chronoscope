package org.timepedia.chronoscope.client;

import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
*/
@ExportPackage("chronoscope")
public class InfoWindowEvent
    extends AbstractEvent {

  public static Type<InfoWindowEvent, InfoWindowClosedHandler> TYPE
      = new Type<InfoWindowEvent, InfoWindowClosedHandler>() {

    protected void fire(InfoWindowClosedHandler infoWindowClosedHandler,
        InfoWindowEvent event) {
      infoWindowClosedHandler.onInfoWindowClosed(event);
    }
  };

  protected Type getType() {
    return TYPE;
  }
}
