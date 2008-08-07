package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.event.client.ClickEvent;
import com.google.gwt.libideas.event.client.ClickHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where the user clicks on the chart.
 *
 * @author Chad Takahashi
 */
public class ChartMouseClickHandler extends AbstractEventHandler<ClickHandler>
    implements ClickHandler {

  public void onClick(ClickEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    chartInfo.maybeDrag = false;
    chart.setAnimating(false);

    int x = getLocalX(event);
    int y = getLocalY(event);
    GWT.log("mouseclick: x=" + x + ", y=" + y, null);
    boolean handled = false;
    if (chart.click(x, y)) {
      // Something on the chart was "hit".  do nothing.
      handled = true;
    }

    ((DOMView) chart.getView()).focus();
    chartInfo.setHandled(true);
  }
}
