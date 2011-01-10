package org.timepedia.chronoscope.client.browser.event;

import org.timepedia.chronoscope.client.Chart;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

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
    chart.setAnimating(false);

    int x = getLocalX(event);
    int y = getLocalY(event);
    chart.click(x, y);

    chartInfo.setHandled(true);
  }
}
