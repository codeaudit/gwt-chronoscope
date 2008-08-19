package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.event.client.BrowserEvent;
import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

/**
 * @author Chad Takahashi
 */
public class OverviewAxisMouseMoveHandler extends
    AbstractEventHandler<MouseMoveHandler> implements MouseMoveHandler {
  
  /**
   * Overrides superclass to provide the Y-value relative to this 
   * component's immediate container (i.e. the DefaultXYPlot.domainPanel).
   * <p>
   * NOTE: This is a workaround until the upcoming Chronoscope 
   * Component/Container classes have been integrated into the UI framework.
   */
  public int getLocalY(BrowserEvent<MouseMoveHandler> event) {
    ChartState chartInfo = getChartState(event);
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    Bounds plotBounds = plot.getBounds();
    return super.getLocalY(event) - (int)plotBounds.bottomY();
  }
  
  public void onMouseMove(MouseMoveEvent event) {
    ChartState chartInfo = getChartState(event);
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    OverviewAxis overviewAxis = plot.getOverviewAxis();
    Bounds hiliteBounds = overviewAxis.getHighlightBounds(); 
    
    int x = getLocalX(event);
    int y = getLocalY(event);
    final boolean isInAxisBounds = overviewAxis.getBounds().inside(x, y);
    
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
    
    if (hiliteBounds != null) {
      if (chartInfo.getCompoundUIAction().isDragging(overviewAxis)) {
        // hiliteLeftDomainX represents the domain-x value of the left edge
        // of the highlight window within the overview axis.
        double hiliteLeftX = x - (hiliteBounds.width / 2.0);
        double hiliteLeftDomainX = toDomainX(hiliteLeftX, plot);
       
        // Need to bound the domain-x value so that the highlight box doesn't
        // run off the overview axis.
        double minHiliteDomain = plot.getDomainMin();
        Bounds overviewAxisBounds = overviewAxis.getBounds();
        double maxHiliteDomain = toDomainX(overviewAxisBounds.rightX() - hiliteBounds.width, plot);
        hiliteLeftDomainX = bound(minHiliteDomain, maxHiliteDomain, hiliteLeftDomainX);

        plot.moveTo(hiliteLeftDomainX);
        plot.redraw();
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
