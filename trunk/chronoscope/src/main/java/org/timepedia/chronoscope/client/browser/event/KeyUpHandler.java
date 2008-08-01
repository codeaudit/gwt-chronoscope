package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.KeyboardListener;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.SafariKeyboardConstants;

/**
 * Handles the event where the user releases a key
 * 
 * @author Chad Takahashi
 */
public final class KeyUpHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    Chart chart = chartInfo.chart;
    int keyCode = DOM.eventGetKeyCode(event);
    boolean handled = true;

    if (keyCode == KeyboardListener.KEY_LEFT ||

        keyCode == KeyboardListener.KEY_PAGEUP
        || keyCode == SafariKeyboardConstants
        .SAFARI_LEFT || keyCode == SafariKeyboardConstants.SAFARI_LEFT
        || keyCode == SafariKeyboardConstants
        .SAFARI_PGUP) {
      chart.pageLeft(keyCode == KeyboardListener.KEY_PAGEUP
          || keyCode == SafariKeyboardConstants.SAFARI_PGUP ? 1.0 : 0.5);
    } else if (keyCode == KeyboardListener.KEY_RIGHT ||

        keyCode == KeyboardListener.KEY_PAGEDOWN
        || keyCode == SafariKeyboardConstants
        .SAFARI_RIGHT || keyCode == SafariKeyboardConstants.SAFARI_RIGHT
        || keyCode == SafariKeyboardConstants
        .SAFARI_PDWN) {
      chart.pageRight(keyCode == KeyboardListener.KEY_PAGEDOWN
          || keyCode == SafariKeyboardConstants.SAFARI_PDWN ? 1.0 : 0.5);
    } else if (keyCode == KeyboardListener.KEY_UP || keyCode == 90 + 32
        || keyCode == SafariKeyboardConstants.SAFARI_UP) {
      chart.nextZoom();
    } else if (keyCode == KeyboardListener.KEY_DOWN
        || keyCode == SafariKeyboardConstants.SAFARI_DOWN
        || keyCode == 88 + 32) {
      chart.prevZoom();
    } else if (keyCode == KeyboardListener.KEY_BACKSPACE) {
      History.back();
    } else if (keyCode == KeyboardListener.KEY_HOME
        || keyCode == SafariKeyboardConstants
        .SAFARI_HOME) {
      chart.maxZoomOut();
    } else {
      handled = false;
    }
    
    return handled;
  }

}
