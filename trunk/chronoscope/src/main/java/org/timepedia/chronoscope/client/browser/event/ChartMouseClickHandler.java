package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.libideas.event.client.ClickHandler;
import com.google.gwt.libideas.event.client.ClickEvent;
import com.google.gwt.libideas.event.client.MouseEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where the user clicks on the chart.
 * 
 * @author Chad Takahashi
 */
public class ChartMouseClickHandler extends AbstractEventHandler<ClickHandler> implements
    ClickHandler {

  public void onClick(ClickEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    chartInfo.maybeDrag = false;
    chart.setAnimating(false);

    int x = getLocalX(event);
    int y = getLocalY(event);

    boolean handled = false;
    if (event.getButton() == MouseEvent.Button.RIGHT) {
      chart.getView().fireContextMenuEvent(x, y);
      handled = true;
    } else if (chart.click(x, y)) {
      // Something on the chart was "hit".  do nothing.
      handled = true;
    }
    
    ((DOMView) chart.getView()).focus();
    chartInfo.setHandled(true);
  }
}
