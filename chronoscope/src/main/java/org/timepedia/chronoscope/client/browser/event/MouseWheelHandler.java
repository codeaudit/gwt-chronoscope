/**
 * 
 */
package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;

/**
 * Handles the event where the user scrolls the mouse wheel.
 * 
 * @author Chad Takahashi
 */
public class MouseWheelHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    
    chartInfo.maybeDrag = false;
    int wheelDir = DOM.eventGetMouseWheelVelocityY(event);
    boolean isMouseWheelUp = (wheelDir <= 0);
    if (isMouseWheelUp) {
      chart.nextZoom();
    }
    else {
      chart.prevZoom();
    }
    
    return true;
  }

}
