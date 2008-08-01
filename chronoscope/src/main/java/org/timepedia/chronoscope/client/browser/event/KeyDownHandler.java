package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;

/**
 * Handles the event where the user depresses a key.
 * 
 * @author Chad Takahashi
 */
public class KeyDownHandler extends AbstractClientEventHandler {

  @Override
  public boolean handle(Event event, int x, int y, ChartState chartInfo) {
    boolean handled = true;
    int keyCode = DOM.eventGetKeyCode(event);
    if (keyCode == KeyboardListener.KEY_PAGEUP
        || keyCode == KeyboardListener.KEY_PAGEDOWN
        || keyCode == KeyboardListener.KEY_UP
        || keyCode == KeyboardListener.KEY_DOWN 
        || keyCode == KEY_TAB) 
    {
      handled = handleTabKey(event, chartInfo, keyCode);
    }
    
    return handled;
  }
}
