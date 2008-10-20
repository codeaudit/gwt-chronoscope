package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.gen2.event.shared.EventHandler;
import com.google.gwt.gen2.event.dom.client.DomEvent;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.ChartEventHandler;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;

/**
 * @author Chad Takahashi
 */
public abstract class AbstractEventHandler<T extends EventHandler> {

  protected static final int KEY_TAB = 9;

  // May get reassigned based on browser type
  protected int tabKeyEventCode = Event.ONKEYDOWN;

  public ChartState getChartState(DomEvent event) {
    return (ChartState) ChartEventHandler.getChartState();
  }
  

  public int getLocalX(DomEvent event) {
    return getChartState(event).getLocalX();
  }

  public int getLocalY(DomEvent event) {
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
    OverviewAxisPanel oaPanel = plot.getOverviewAxisPanel();
    if (oaPanel != null && oaPanel.getBounds().inside(overviewAxisX, overviewAxisY)) {
      return oaPanel.getValueAxis();
    }
    
    return null;
  }
}
