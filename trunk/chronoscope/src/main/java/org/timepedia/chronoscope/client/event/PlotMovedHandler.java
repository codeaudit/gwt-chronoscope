package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 * Implement to handle PlotMovedEvent.
 */
public interface PlotMovedHandler extends EventHandler {
  void onMoved(PlotMovedEvent event);
}