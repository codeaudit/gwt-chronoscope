package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 *
 */
public interface ChartDragStartHandler extends EventHandler {

  void onDragStart(ChartDragStartEvent event);
}
