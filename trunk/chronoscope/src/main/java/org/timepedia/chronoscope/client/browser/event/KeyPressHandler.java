package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;

import org.timepedia.chronoscope.client.Chart;

/**
 * Handles the event where a character is generated from a key press (either 
 * directory or through auto-repeat).
 * 
 * @author Chad Takahashi
 */
public class KeyPressHandler extends AbstractClientEventHandler {
  private static final int KEY_S = 83 + 32;
  private static final int KEY_X = 88 + 32;
  private static final int KEY_Z = 90 + 32;

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    
    int keyCode = DOM.eventGetKeyCode(event);
    boolean handled = true;
    
    if (keyCode == KEY_TAB) {
      handled = handleTabKey(event, chartInfo, keyCode);
    } else if (keyCode == KEY_Z) {
      chart.nextZoom();
    } else if (keyCode == KEY_X) {
      chart.prevZoom();
    } else if (keyCode == KEY_S) {
      chartInfo.selActive = !chartInfo.selActive;
    } else if (keyCode == KeyboardListener.KEY_ENTER) {
      chart.maxZoomToFocus();
    } else {
      handled = false;
    }
    
    return handled;
  }

}
