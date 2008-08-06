package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.shared.AbstractEvent;
import com.google.gwt.libideas.event.shared.EventHandler;

/**
 * A handle to a Window opened via the View.openInfoWindow method.
 */
public interface InfoWindow {


  void close();

  void setPosition(double x, double y);

  void addInfoWindowClosedHandler(InfoWindowClosedHandler handler);

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
