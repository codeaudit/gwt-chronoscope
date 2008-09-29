package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.BrowserEvent;
import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseMoveHandler;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.MathUtil;

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
  @Override
  public int getLocalY(BrowserEvent<MouseMoveHandler> event) {
    ChartState chartInfo = getChartState(event);
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    Bounds plotBounds = plot.getBounds();
    return super.getLocalY(event) - (int)plotBounds.bottomY();
  }

  public void onMouseMove(MouseMoveEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    DefaultXYPlot plot = (DefaultXYPlot)chartInfo.chart.getPlot();
    
    if (!plot.isOverviewEnabled()) {
      return;
    }
    
    OverviewAxis overviewAxis = plot.getOverviewAxis();
    Bounds overviewAxisBounds = overviewAxis.getBounds();
    Bounds hiliteBounds = overviewAxis.getHighlightBounds(); 
    CompoundUIAction uiAction = chartInfo.getCompoundUIAction();
    
    int x = getLocalX(event);
    int y = getLocalY(event);
    final boolean isInAxisBounds = overviewAxisBounds.inside(x, y);
    final boolean isDragging = uiAction.isDragging(overviewAxis);

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
    
    if (uiAction.isSelecting(overviewAxis)) {
      chart.setCursor(Cursor.SELECTING);
      chart.setAnimating(true);
      // Determine the start and end domain of the highlight selection
      double startDomainX = toDomainX(uiAction.getStartX(), plot);
      double boundedX = MathUtil.bound(x, overviewAxisBounds.x, overviewAxisBounds.rightX());
      double endDomainX = toDomainX(boundedX, plot);
      if (startDomainX > endDomainX) {
        double tmp = startDomainX;
        startDomainX = endDomainX;
        endDomainX = tmp;
      }
      // Set the current highlight regeion in the plot
      plot.setHighlight(startDomainX, endDomainX);
      plot.setDomainOrigin(startDomainX);
      plot.setCurrentDomain(endDomainX - startDomainX);
      plot.redraw();
    }
    else if (hiliteBounds != null) {
      if (isDragging) {
        // hiliteLeftDomainX represents the domain-x value of the left edge
        // of the highlight window within the overview axis.
        double hiliteLeftX = x - (hiliteBounds.width / 2.0);
        double hiliteLeftDomainX = toDomainX(hiliteLeftX, plot);
       
        // Need to bound the domain-x value so that the highlight box doesn't
        // run off the overview axis.
        double minHiliteDomain = plot.getDomainMin();
        double maxHiliteDomain = toDomainX(overviewAxisBounds.rightX() - hiliteBounds.width, plot);
        hiliteLeftDomainX = MathUtil.bound(hiliteLeftDomainX, minHiliteDomain, maxHiliteDomain);

        plot.moveTo(hiliteLeftDomainX);
        plot.redraw();
      }
    }
  }
  
  /**
   * Converts the specified window-X value to a domain-X value.
   */
  private static double toDomainX(double windowX, DefaultXYPlot plot) {
    double userX = plot.windowXtoUser(windowX);
    return plot.getOverviewAxis().userToData(userX);
  }

}
