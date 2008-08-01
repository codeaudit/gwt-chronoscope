package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where the user releases the mouse button.
 * 
 * @author Chad Takahashi
 */
public final class MouseUpHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;

    if (chartInfo.selActive) {
      chartInfo.selActive = false;
      chart.setAnimating(false);
      chartInfo.selStart = -1;
      if (shiftKeyPressed(event)) {
        chart.zoomToHighlight();
      }
    } else if (chartInfo.maybeDrag && x != chartInfo.dragStart) {
      ((DOMView) chart.getView()).pushHistory();
      chart.setAnimating(false);
      chart.redraw();
    }

    chart.setCursor(Cursor.DRAGGING);
    chartInfo.maybeDrag = false;
    ((DOMView) chart.getView()).focus();

    return true;
  }
}