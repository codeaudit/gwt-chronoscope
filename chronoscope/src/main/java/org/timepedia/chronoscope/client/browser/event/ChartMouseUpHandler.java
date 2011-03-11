package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.HistoryManager;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.DOMView;
import org.timepedia.chronoscope.client.event.ChartDragEndEvent;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

import com.google.gwt.user.client.Event;
/**
 * Handles the event where the user releases the mouse button.
 *
 * @author Chad Takahashi
 */
public final class ChartMouseUpHandler
    extends AbstractEventHandler<MouseUpHandler> implements MouseUpHandler {

  public void onMouseUp(MouseUpEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    XYPlot plot = chart.getPlot();
    int x = getLocalX(event);
    int y = getLocalY(event);
    OverviewAxisMouseMoveHandler.setHiliteRelativeGrabX(Double.NaN);

    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    if (uiAction.isSelecting()) {
      chart.setAnimating(false);
      chart.zoomToHighlight();
    } else if (uiAction.getSource() != null && uiAction.isDragging(uiAction.getSource()) && x != uiAction.getDragStartX()) {
      if (uiAction.getSource() instanceof Overlay) {
        ((Overlay) uiAction.getSource()).fire(new ChartDragEndEvent(plot, x));
      } else {
        HistoryManager.pushHistory();
      }
      chart.setAnimating(false);
      
      // FIXME: this produces several unneeded redraws, if we remove this, dragging does not 
      // redraw correctly, so algorithm  in mouse move should be reviewd
      ((DefaultXYPlot) chart.getPlot()).redraw(true);
    }

    chartInfo.getCompoundUIAction().cancel();
    chart.setCursor(Cursor.DEFAULT);

    if (event.getNativeButton() == Event.BUTTON_RIGHT) {
      ((DefaultXYPlot) chart.getPlot()).fireContextMenuEvent(x, y);
    } else {
      // fire click, in mouse up events this fixes click on flash view.
      chart.click(x, y);
    }

    chartInfo.setHandled(true);
  }
}