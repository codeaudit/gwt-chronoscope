package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.KeyUpEvent;
import com.google.gwt.libideas.event.client.KeyUpHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.KeyboardListener;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.SafariKeyboardConstants;

/**
 * Handles the event where the user releases a key
 * 
 * @author Chad Takahashi
 */
public final class ChartKeyUpHandler extends AbstractEventHandler<KeyUpHandler> implements
    KeyUpHandler {

  public void onKeyUp(KeyUpEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int keyCode = event.getKeyCode();
    boolean handled = true;

    if (  keyCode == KeyboardListener.KEY_LEFT 
        || keyCode == KeyboardListener.KEY_PAGEUP
        || keyCode == SafariKeyboardConstants.SAFARI_LEFT 
        || keyCode == SafariKeyboardConstants.SAFARI_LEFT
        || keyCode == SafariKeyboardConstants.SAFARI_PGUP) {
      chart.pageLeft(keyCode == KeyboardListener.KEY_PAGEUP
          || keyCode == SafariKeyboardConstants.SAFARI_PGUP ? 1.0 : 0.5);
    } else if (keyCode == KeyboardListener.KEY_RIGHT
        || keyCode == KeyboardListener.KEY_PAGEDOWN
        || keyCode == SafariKeyboardConstants.SAFARI_RIGHT 
        || keyCode == SafariKeyboardConstants.SAFARI_RIGHT
        || keyCode == SafariKeyboardConstants.SAFARI_PDWN) {
      chart.pageRight(keyCode == KeyboardListener.KEY_PAGEDOWN
          || keyCode == SafariKeyboardConstants.SAFARI_PDWN ? 1.0 : 0.5);
    } else if (keyCode == KeyboardListener.KEY_UP 
        || keyCode == ChartKeyPressHandler.KEY_Z
        || keyCode == SafariKeyboardConstants.SAFARI_UP) {
      chart.nextZoom();
    } else if (keyCode == KeyboardListener.KEY_DOWN
        || keyCode == SafariKeyboardConstants.SAFARI_DOWN
        || keyCode == ChartKeyPressHandler.KEY_X) {
      chart.prevZoom();
    } else if (keyCode == KeyboardListener.KEY_BACKSPACE) {
      History.back();
    } else if (keyCode == KeyboardListener.KEY_HOME
        || keyCode == SafariKeyboardConstants.SAFARI_HOME) {
      chart.maxZoomOut();
    } else {
      handled = false;
    }
    
    chartInfo.setHandled(handled);
  }

}
