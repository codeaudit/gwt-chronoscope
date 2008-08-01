package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the mouse if moved outside the chart area.
 * 
 * @author Chad Takahashi
 */
public class MouseOutHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    chart.setAnimating(false);
    chart.setCursor(Cursor.DEFAULT);
    chart.redraw();
    return true;
  }

}
