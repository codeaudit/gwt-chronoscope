package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 * Implement to handle PlotHoverEvent.
 */
public interface PlotHoverHandler extends EventHandler {
  void onHover(PlotHoverEvent event);
}
