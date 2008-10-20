package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.gen2.event.dom.client.MouseOutHandler;
import com.google.gwt.gen2.event.dom.client.MouseOutEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the mouse if moved outside the chart area.
 *
 * @author Chad Takahashi
 */
public class ChartMouseOutHandler extends AbstractEventHandler<MouseOutHandler>
    implements MouseOutHandler {

  public void onMouseOut(MouseOutEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    chart.setAnimating(false);
    chart.setCursor(Cursor.DEFAULT);
    chart.redraw();
    chartInfo.setHandled(true);
  }
}
