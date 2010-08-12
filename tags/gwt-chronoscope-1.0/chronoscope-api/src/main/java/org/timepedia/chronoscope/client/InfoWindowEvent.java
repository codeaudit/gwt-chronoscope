package org.timepedia.chronoscope.client;

import com.google.gwt.event.shared.GwtEvent;

import org.timepedia.exporter.client.ExportPackage;

/**
*/
@ExportPackage("chronoscope")
public class InfoWindowEvent
    extends GwtEvent<InfoWindowClosedHandler> {

  public static Type<InfoWindowClosedHandler> TYPE
      = new Type<InfoWindowClosedHandler>();

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(InfoWindowClosedHandler infoWindowClosedHandler) {
    infoWindowClosedHandler.onInfoWindowClosed(this);
  }
}
