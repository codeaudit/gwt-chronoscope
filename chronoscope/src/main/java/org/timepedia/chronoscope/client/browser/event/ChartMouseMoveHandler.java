package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;

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
    XYPlot plot = chart.getPlot();
    int x = getLocalX(event);
    int y = getLocalY(event);
    
    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    if (uiAction.isSelecting(plot)) {
      chart.setAnimating(true);
      chart.setHighlight(uiAction.getStartX(), x);
    } else if (uiAction.isDragging(plot)) {
      boolean dragThresholdReached = Math.abs(uiAction.getStartX() - x) > 10;
      if (dragThresholdReached) {
        chart.setAnimating(true);
        chart.scrollPixels(uiAction.getStartX() - x);
        uiAction.setStartX(x);
        event.getBrowserEvent().cancelBubble(true);
        event.getBrowserEvent().preventDefault();
      }
    } else if (plot.getBounds().inside(x, y)) {
      if (chart.setHover(x, y)) {
        chart.setCursor(Cursor.CLICKABLE);
      } else {
        chart.setCursor(Cursor.DRAGGABLE);
      }
    }
    
    chartInfo.setHandled(true);
  }

}
