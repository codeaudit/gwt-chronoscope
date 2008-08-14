package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

/**
 * @author Chad Takahashi
 */
public class OverviewAxisMouseMoveHandler extends
    AbstractEventHandler<MouseMoveHandler> implements MouseMoveHandler {

  public void onMouseMove(MouseMoveEvent event) {
    ChartState chartInfo = getChartState(event);
    XYPlot plot = chartInfo.chart.getPlot();
    Bounds plotBounds = plot.getPlotBounds();
    OverviewAxis axis = plot.getOverviewAxis();
    Bounds highlightBounds = axis.getHighlightBounds(); 
    
    if (highlightBounds != null) {
      int x = getLocalX(event);
      int y = getLocalY(event) - (int)(plotBounds.y + plotBounds.height);
      
      if (highlightBounds.inside(x, y)) {
        chartInfo.chart.setCursor(Cursor.DRAGGABLE);
      }
      
      if (axis.getBounds().inside(x, y)) {
        boolean isDragging = chartInfo.isMouseDown;
        if (isDragging) { // drag = mouseDown + mouseMove
          // hiliteLeftX represents the x-value of the left vertical edge
          //of the highlight window within the overview axis.
          double halfHiliteWidth = highlightBounds.width / 2.0;
          double hiliteLeftX = x - halfHiliteWidth;
          
          double hiliteLeftDomainX = windowToDomainX(hiliteLeftX, (DefaultXYPlot)plot);
          hiliteLeftDomainX = Math.max(hiliteLeftDomainX, plot.getDomainMin());
          plot.moveTo(hiliteLeftDomainX);
        }
      }
    }
  }
  
  private double windowToDomainX(double windowX, DefaultXYPlot plot) {
    double userX = plot.windowXtoUser(windowX);
    return plot.getOverviewAxis().userToData(userX);
  }

}
