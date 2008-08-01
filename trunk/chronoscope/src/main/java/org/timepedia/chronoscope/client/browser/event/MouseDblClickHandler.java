package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;

/**
 * Handles the event where  the user double-clicks on the chart.
 * 
 * @author Chad Takahashi
 */
public class MouseDblClickHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;

    chartInfo.maybeDrag = false;
    chart.setAnimating(false);
    
    boolean handled = false;
    if (chart.maxZoomTo(x, y)) {
      DOM.eventCancelBubble(event, true);
      DOM.eventPreventDefault(event);
      handled = true;
    }

    return handled;
  }

}
