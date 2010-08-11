package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gen2.event.dom.client.ClickHandler;
import com.google.gwt.gen2.event.dom.client.ClickEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.DOMView;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

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

    ((DOMView) chart.getView()).focus();
    ((DefaultXYPlot)chart.getPlot()).redraw(true);
    chartInfo.setHandled(true);
  }
}
