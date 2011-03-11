package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.event.ChartDragEvent;
import org.timepedia.chronoscope.client.event.ChartDragStartEvent;

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
      plot.setHighlight(uiAction.getStartX(), x);
    } else if (uiAction.getSource() != null && uiAction
        .isDragging(uiAction.getSource())) {
      int dragThd = uiAction.isDragStarted(plot) ? 1
          : (uiAction.getSource() instanceof Overlay ? 5 : 10);

      boolean dragThresholdReached = Math.abs(uiAction.getStartX() - x)
          > dragThd;
      if (dragThresholdReached) {
        if (uiAction.getSource() instanceof Overlay) {
          if (!uiAction.isDragStarted(plot)) {
            plot.fireEvent(new ChartDragStartEvent(plot, x));
            uiAction.setDragStarted(true);
            uiAction.setDragStartX(uiAction.getStartX());
          }
          ((Overlay) uiAction.getSource()).fire(new ChartDragEvent(plot, x));
          chart.setHover(x,y);
        } else {
          chart.setAnimating(true);
          chart.scrollPixels(uiAction.getStartX() - x);
        }
        uiAction.setStartX(x);
        event.stopPropagation();
        event.preventDefault();
      }
    } else if ((null != plot) && (null != plot.getBounds()) && (plot.getBounds().inside(x, y))) {
      if (chart.setHover(x, y)) {
        chart.setCursor(Cursor.CLICKABLE);
      } else {
        chart.setCursor(Cursor.DRAGGABLE);
      }
    }

    chartInfo.setHandled(true);
  }
}
