package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.BrowserEvent;
import com.google.gwt.libideas.event.shared.EventHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;

/**
 * @author Chad Takahashi
 */
public abstract class AbstractEventHandler<T extends EventHandler> {

  protected static final int KEY_TAB = 9;

  // May get reassigned based on browser type
  protected int tabKeyEventCode = Event.ONKEYDOWN;

  public ChartState getChartState(BrowserEvent<T> event) {
    return (ChartState) event.getUserData();
  }

  public int getLocalX(BrowserEvent<T> event) {
    return getChartState(event).getLocalX();
  }

  public int getLocalY(BrowserEvent<T> event) {
    return getChartState(event).getLocalY();
  }

  /**
   * Handles TAB key and Shift-TAB key presses.
   */
  protected boolean handleTabKey(Event event, ChartState chartInfo,
      int keyCode, boolean isShiftKeyDown) {
    if (DOM.eventGetType(event) != chartInfo.getTabKeyEventCode()) {
      return false;
    }

    if (keyCode == KEY_TAB) {
      if (isShiftKeyDown) {
        chartInfo.chart.prevFocus();
      } else {
        chartInfo.chart.nextFocus();
      }
      return true;
    }

    return false;
  }
  
  /**
   * Returns the component that the specified (x,y) chart coordinate is on.  
   * If the (x,y) point does not fall on a recognized component, then null 
   * is returned.
   */
  protected Object getComponent(int x, int y, XYPlot plot) {
    Bounds plotBounds = plot.getBounds();
    
    if (plotBounds.inside(x, y)) {
      return plot;
    }
    
    int overviewAxisX = x;
    int overviewAxisY = (int)(y - plotBounds.bottomY());
    OverviewAxis oa = plot.getOverviewAxis();
    if (oa != null && oa.getBounds().inside(overviewAxisX, overviewAxisY)) {
      return oa;
    }
    
    return null;
  }
}
