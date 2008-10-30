package org.timepedia.chronoscope.client.browser.event;


import com.google.gwt.gen2.event.dom.client.MouseEvent;
import com.google.gwt.gen2.event.dom.client.MouseUpHandler;
import com.google.gwt.gen2.event.dom.client.MouseUpEvent;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.HistoryManager;
import org.timepedia.chronoscope.client.browser.DOMView;

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
    
    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    if (uiAction.isSelecting()) {
      chart.setAnimating(false);
      chart.zoomToHighlight();
    }
    else if (uiAction.isDragging(plot) && x != uiAction.getStartX()) {
      HistoryManager.pushHistory();
      chart.setAnimating(false);
      chart.redraw();
    }

    chartInfo.getCompoundUIAction().cancel();
    chart.setCursor(Cursor.DEFAULT);

    ((DOMView) chart.getView()).focus();
    
    if (event.getButton() == MouseEvent.Button.RIGHT) {
      chart.getView().fireContextMenuEvent(x, y);
    }
    
    chartInfo.setHandled(true);
  }
}