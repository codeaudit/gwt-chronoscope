package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.BrowserEvent;
import com.google.gwt.libideas.event.shared.EventHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

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
    if (DOM.eventGetType(event) != chartInfo.tabKeyEventCode) {
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

}
