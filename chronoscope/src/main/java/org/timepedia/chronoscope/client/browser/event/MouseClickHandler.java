package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where the user clicks on the chart.
 * 
 * @author Chad Takahashi
 */
public class MouseClickHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    chartInfo.maybeDrag = false;
    chart.setAnimating(false);

    boolean handled = false;
    if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
      chart.getView().fireContextMenuEvent(x, y);
      handled = true;
    } else if (chart.click(x, y)) {
      // Something on the chart was "hit".  do nothing.
      handled = true;
    }
    
    ((DOMView) chart.getView()).focus();
    return handled;
  }

}
