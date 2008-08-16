package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.MouseOverEvent;
import com.google.gwt.libideas.event.client.MouseOverHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.DOMView;

/**
 * Handles the event where mouse is moved into the chart area.
 *
 * @author Chad Takahashi
 */
public class ChartMouseOverHandler
    extends AbstractEventHandler<MouseOverHandler> implements MouseOverHandler {

  public void onMouseOver(MouseOverEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int x = getLocalX(event);
    int y = getLocalY(event);
    chart.setPlotFocus(x, y);
    boolean isMouseInPlot = chart.getPlot().getBounds().inside(x, y);
    chart.setCursor(
        isMouseInPlot ? Cursor.DRAGGABLE : Cursor.DEFAULT);
    ((DOMView) chart.getView()).focus();
    chartInfo.maybeDrag = false;
    chartInfo.setHandled(true);
  }

}
