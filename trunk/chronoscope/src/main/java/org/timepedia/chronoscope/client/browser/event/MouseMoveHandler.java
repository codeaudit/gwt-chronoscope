package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the mouse is moved within the chart area.
 * 
 * @author Chad Takahashi
 */
public final class MouseMoveHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;

    if (chart.isInsidePlot(x, y)) {
      if (chartInfo.selActive && chartInfo.selStart > -1) {
        chart.setCursor(Cursor.SELECTING);
        chart.setAnimating(true);
        chart.setHighlight(chartInfo.selStart, x);
      } else {
        if (chartInfo.maybeDrag && Math.abs(chartInfo.dragStart - x) > 10) {
          chart.setAnimating(true);
          chart.setCursor(Cursor.DRAGGING);
          chart.scrollPixels(chartInfo.dragStart - x);
          chartInfo.dragStart = x;
          DOM.eventCancelBubble(event, true);
          DOM.eventPreventDefault(event);
        } else {
          if (chart.setHover(x, y)) {
            chart.setCursor(Cursor.CLICKABLE);
          } else {
            chart.setCursor(Cursor.DRAGGABLE);
          }
        }
      }
    } else {
      chart.setCursor(Cursor.DEFAULT);
    }

    // else if (maybeDrag && chart.getOverviewBounds().inside(x, y)) {
    // chart.getOverviewAxis().drag(view, startDragX, x, y);
    // }

    return true;
  }

}
