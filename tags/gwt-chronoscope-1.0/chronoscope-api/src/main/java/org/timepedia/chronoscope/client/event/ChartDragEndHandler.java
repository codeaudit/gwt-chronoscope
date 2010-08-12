package org.timepedia.chronoscope.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 */
public interface ChartDragEndHandler extends EventHandler {

  void onDragEnd(ChartDragEndEvent event);
}
