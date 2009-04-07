package org.timepedia.chronoscope.client.event;

import com.google.gwt.gen2.event.shared.EventHandler;

/**
 *
 */
public interface ChartDragHandler extends EventHandler {

  void onDrag(ChartDragEvent event);
}
