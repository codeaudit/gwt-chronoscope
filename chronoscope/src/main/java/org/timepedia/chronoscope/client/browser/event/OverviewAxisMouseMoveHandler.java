package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * @author Chad Takahashi
 */
public class OverviewAxisMouseMoveHandler extends
  AbstractEventHandler<MouseMoveHandler> implements MouseMoveHandler {

 private static int hiliteRelativeGrabX;
    
  public void onMouseMove(MouseMoveEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    
    if (!plot.isOverviewVisible()) {
      return;
    }
    
    OverviewAxisPanel oaPanel = plot.getOverviewAxisPanel();
    ValueAxis overviewAxis = oaPanel.getValueAxis();
    Bounds overviewAxisBounds = oaPanel.getBounds();
    Bounds hiliteBounds = oaPanel.getHighlightBounds(); 
    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    
    // Translate the view (x,y) point into an (x,y) point relative to
    // the OverviewAxisPanel.
    Bounds plotBounds = plot.getBounds();
    int x = getLocalX(event) - (int)plotBounds.x;
    int y = getLocalY(event) - (int)plotBounds.bottomY();
    
    final boolean isInAxisBounds = overviewAxisBounds.inside(x, y);
    final boolean isDragging = uiAction.isDragging(overviewAxis);
    
    //log("onMouseMove(" + x + ", " + y + ")" + ": inAxisBounds=" + isInAxisBounds + "; uiAction.startX=" + uiAction.getEndX());
    
    // Determine appropriate cursor type
    if (isInAxisBounds) {
      if (hiliteBounds != null && hiliteBounds.inside(x, y)) {
        chartInfo.chart.setCursor(Cursor.DRAGGABLE);
      }
      else {
        chartInfo.chart.setCursor(Cursor.DEFAULT);
      }
    }
    // else, mouse is outside of overview axis, so don't mess with cursor.
    
    final double viewOffsetX = plotBounds.x;
    
    if (uiAction.isSelecting(overviewAxis)) {
      chart.setAnimating(true);

      chart.setCursor(Cursor.SELECTING);
      chart.setAnimating(true);
      // Determine the start and end domain of the highlight selection
      double startDomainX = toDomainX(uiAction.getStartX(), plot);
      double boundedX = MathUtil.bound(x, overviewAxisBounds.x, overviewAxisBounds.rightX());
      double endDomainX = toDomainX(boundedX + viewOffsetX, plot);
      if (startDomainX > endDomainX) {
        double tmp = startDomainX;
        startDomainX = endDomainX;
        endDomainX = tmp;
      }
      // Set the current highlight region in the plot
      plot.setHighlight(startDomainX, endDomainX);
      plot.getDomain().setEndpoints(startDomainX, endDomainX);
      plot.redraw();
    }
    else if (hiliteBounds != null && isDragging) {
      chart.setAnimating(true);
      
      // hiliteLeftDomainX represents the domain-x value of the left edge
      // of the highlight window within the overview axis.
      //double hiliteLeftX = x - (hiliteBounds.width / 2.0);x will always be at the center of the hiliteBounds

      double hiliteLeftX = x - hiliteRelativeGrabX;
      double hiliteLeftDomainX = toDomainX(hiliteLeftX + viewOffsetX, plot);
     
      // Need to bound the domain-x value so that the highlight box doesn't
      // run off the overview axis.
      double minHiliteDomain = plot.getWidestDomain().getStart();
      double maxHiliteDomain = toDomainX(overviewAxisBounds.rightX() + viewOffsetX - hiliteBounds.width, plot);

      hiliteLeftDomainX = MathUtil.bound(hiliteLeftDomainX, minHiliteDomain, maxHiliteDomain);

      plot.moveTo(hiliteLeftDomainX);
      // hiliteRelativeGrabX(plot, x);
    }
  }
  
  /**
   * Converts the specified window-X value to a domain-X value.
   */
  private static double toDomainX(double windowX, DefaultXYPlot plot) {
    double userX = plot.windowXtoUser(windowX);
    return plot.getOverviewAxisPanel().getValueAxis().userToData(userX);
  }

  /**
   * records the initial X value position of the grab relative to the hilite region (used to reset to middle)
   */
  public static void hiliteRelativeGrabX(XYPlot xyPlot, int x) {
    int hiliteX = (int) xyPlot.getOverviewAxisPanel().getHighlightBounds().x;
    hiliteRelativeGrabX = x - hiliteX;
  }
  
  private static void log(Object msg) {
    System.out.println("OverviewAxisMouseMoveHandler> " + msg);
  }
}
