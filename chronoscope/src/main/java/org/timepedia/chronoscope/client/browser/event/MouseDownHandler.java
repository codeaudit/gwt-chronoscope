package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the user depresses a mouse button.
 * 
 * @author Chad Takahashi
 */
public final class MouseDownHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;

    if (chartInfo.selActive || shiftKeyPressed(event)) {
      chartInfo.selStart = x;
      chartInfo.selActive = true;
      chart.setCursor(Cursor.SELECTING);
    } else {
      chartInfo.maybeDrag = true;
      chartInfo.dragStart = x;
    }
    chart.setPlotFocus(x, y);

    return true;
  }
}

