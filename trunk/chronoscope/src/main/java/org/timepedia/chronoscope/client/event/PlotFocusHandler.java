package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 * Implement to handle PlotFocusEvent.
 */
public interface PlotFocusHandler extends EventHandler {
  void onFocus(PlotFocusEvent event);
}