package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where mouse is moved into the chart area.
 * 
 * @author Chad Takahashi
 */
public class MouseOverHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    chart.setPlotFocus(x, y);
    chart.setCursor(chart.isInsidePlot(x, y) ? Cursor.DRAGGABLE
        : Cursor.DEFAULT);
    ((DOMView) chart.getView()).focus();
    chartInfo.maybeDrag = false;
    return true;
  }

}
