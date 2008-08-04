package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;

/**
 * Handles the event where the mouse is moved within the chart area.
 *
 * @author Chad Takahashi
 */
public final class ChartMouseMoveHandler
    extends AbstractEventHandler<MouseMoveHandler> implements MouseMoveHandler {

  public void onMouseMove(MouseMoveEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int x = getLocalX(event);
    int y = getLocalY(event);

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
          event.getBrowserEvent().cancelBubble(true);
          event.getBrowserEvent().preventDefault();
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

    chartInfo.setHandled(true);
  }

}
