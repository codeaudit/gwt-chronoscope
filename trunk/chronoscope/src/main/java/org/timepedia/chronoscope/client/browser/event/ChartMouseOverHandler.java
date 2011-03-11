package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;

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
    boolean isMouseInPlot = false;
    if ((null != chart) && (null != chart.getPlot()) && (null != chart.getPlot().getBounds())) {
      isMouseInPlot = chart.getPlot().getBounds().inside(x, y);
    }
    chart.setCursor(
        isMouseInPlot ? Cursor.DRAGGABLE : Cursor.DEFAULT);
    //((DOMView) chart.getView()).focus();
    chartInfo.setHandled(true);
  }

}
