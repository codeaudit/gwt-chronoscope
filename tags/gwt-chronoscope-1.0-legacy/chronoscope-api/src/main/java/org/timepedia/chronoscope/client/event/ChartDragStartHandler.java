package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 */
public interface ChartDragStartHandler extends EventHandler {

  void onDragStart(ChartDragStartEvent event);
}
