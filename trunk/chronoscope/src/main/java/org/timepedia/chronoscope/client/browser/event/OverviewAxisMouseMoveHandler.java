package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.core.client.GWT;
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
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    Bounds plotBounds = plot.getBounds();
    OverviewAxis overviewAxis = plot.getOverviewAxis();
    Bounds hiliteBounds = overviewAxis.getHighlightBounds(); 
    
    if (hiliteBounds != null) {
      // The (x,y) coordinate provided by the event needs to be transformed
      // into the same coordinate space as the the overview axis and associated
      // highlightBounds.
      int x = getLocalX(event);
      int y = getLocalY(event) - (int)(plotBounds.y + plotBounds.height);
      
      if (hiliteBounds.inside(x, y)) {
        chartInfo.chart.setCursor(Cursor.DRAGGABLE);
      }
      
      Bounds overviewAxisBounds = overviewAxis.getBounds();
      if (overviewAxisBounds.inside(x, y)) {
        boolean isDragging = chartInfo.isMouseDown;
        if (isDragging) { // drag = mouseDown + mouseMove
          // hiliteLeftDomainX represents the domain-x value of the left edge
          // of the highlight window within the overview axis.
          double hiliteLeftX = x - (hiliteBounds.width / 2.0);
          double hiliteLeftDomainX = toDomainX(hiliteLeftX, plot);
         
          // Need to bound the domain-x value so that the highlight box doesn't
          // run off the overview axis.
          double minHiliteDomain = plot.getDomainMin();
          double maxHiliteDomain = toDomainX(overviewAxisBounds.rightX() - hiliteBounds.width, plot);
          hiliteLeftDomainX = bound(minHiliteDomain, maxHiliteDomain, hiliteLeftDomainX);

          plot.moveTo(hiliteLeftDomainX);
          plot.redraw();
        }
      }
    }
  }
  
  private double bound(double min, double max, double value) {
    if (value > max) {
      return max;
    }
    if (value < min) {
      return min;
    }
    return value;
  }
  
  /**
   * Converts the specified window-X value to a domain-X value.
   */
  private double toDomainX(double windowX, DefaultXYPlot plot) {
    double userX = plot.windowXtoUser(windowX);
    return plot.getOverviewAxis().userToData(userX);
  }

}
