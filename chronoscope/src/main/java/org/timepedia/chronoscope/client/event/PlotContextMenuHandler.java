package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Implement to handle PlotContentMenuEvent.
 */
public interface PlotContextMenuHandler extends EventHandler {
  void onContextMenu(PlotContextMenuEvent event);
}