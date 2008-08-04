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
      int keyCode) {
    if (DOM.eventGetType(event) != chartInfo.tabKeyEventCode) {
      return false;
    }

    if (keyCode == KEY_TAB) {
      if (shiftKeyPressed(event)) {
        chartInfo.chart.prevFocus();
      } else {
        chartInfo.chart.nextFocus();
      }
      return true;
    }

    return false;
  }

  /**
   * Convenience method that returns true only if the shift key was being
   * pressed at the time the event was fired.
   */
  protected boolean shiftKeyPressed(Event e) {
    return DOM.eventGetShiftKey(e);
  }
}
