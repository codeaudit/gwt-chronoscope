/**
 * 
 */
package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.libideas.event.client.MouseWheelHandler;
import com.google.gwt.libideas.event.client.MouseWheelEvent;

import org.timepedia.chronoscope.client.Chart;

/**
 * Handles the event where the user scrolls the mouse wheel.
 * 
 * @author Chad Takahashi
 */
public class ChartMouseWheelHandler extends AbstractEventHandler<MouseWheelHandler> implements MouseWheelHandler {

  public void onMouseWheel(MouseWheelEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;

    int wheelDir = event.getMouseWheelVelocityY();
    boolean isMouseWheelUp = (wheelDir <= 0);
    if (isMouseWheelUp) {
      chart.nextZoom();
    }
    else {
      chart.prevZoom();
    }
    
    chartInfo.setHandled(true);
  }

}
