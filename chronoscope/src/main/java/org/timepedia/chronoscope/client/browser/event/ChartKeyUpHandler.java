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
  
  private static final double FULL_PAGE_SCROLL = 1.0;
  private static final double HALF_PAGE_SCROLL = 0.5;
  
  public void onKeyUp(KeyUpEvent event) {
    ChartState chartInfo = getChartState(event);
    Chart chart = chartInfo.chart;
    int keyCode = event.getKeyCode();
    boolean handled = true;

    if (isPageUp(keyCode)) {
      chart.pageLeft(FULL_PAGE_SCROLL);
    } else if (isKeyLeft(keyCode)) {
      chart.pageLeft(HALF_PAGE_SCROLL);
    } else if (isPageDown(keyCode)) {
      chart.pageRight(FULL_PAGE_SCROLL);
    } else if (isKeyRight(keyCode)) {
      chart.pageRight(HALF_PAGE_SCROLL);
    } else if (isNextZoom(keyCode)) {
      chart.nextZoom();
    } else if (isPrevZoom(keyCode)) {
      chart.prevZoom();
    } else if (keyCode == KeyboardListener.KEY_BACKSPACE) {
      History.back();
    } else if (isMaxZoomOut(keyCode)) {
      chart.maxZoomOut();
    } else {
      handled = false;
    }
    
    chartInfo.setHandled(handled);
  }

  
  private static boolean isNextZoom(int keyCode) {
    return keyCode == KeyboardListener.KEY_UP 
    || keyCode == ChartKeyPressHandler.KEY_Z
    || keyCode == SafariKeyboardConstants.SAFARI_UP;
  }
  
  private static boolean isPrevZoom(int keyCode) {
    return keyCode == KeyboardListener.KEY_DOWN
    || keyCode == SafariKeyboardConstants.SAFARI_DOWN
    || keyCode == ChartKeyPressHandler.KEY_X;   
  }
  
  private static boolean isMaxZoomOut(int keyCode) {
    return keyCode == KeyboardListener.KEY_HOME
    || keyCode == SafariKeyboardConstants.SAFARI_HOME;
  }
  
  private static boolean isKeyLeft(int keyCode) {
    return keyCode == KeyboardListener.KEY_LEFT 
    || keyCode == SafariKeyboardConstants.SAFARI_LEFT 
    || keyCode == SafariKeyboardConstants.SAFARI_LEFT;
  }

  private static boolean isKeyRight(int keyCode) {
    return keyCode == KeyboardListener.KEY_RIGHT
    || keyCode == SafariKeyboardConstants.SAFARI_RIGHT 
    || keyCode == SafariKeyboardConstants.SAFARI_RIGHT;
  }
  
  private static boolean isPageUp(int keyCode) {
    return keyCode == KeyboardListener.KEY_PAGEUP
      || keyCode == SafariKeyboardConstants.SAFARI_PGUP;    
  }

  private static boolean isPageDown(int keyCode) {
    return keyCode == KeyboardListener.KEY_PAGEDOWN
      || keyCode == SafariKeyboardConstants.SAFARI_PDWN;    
  }
}
