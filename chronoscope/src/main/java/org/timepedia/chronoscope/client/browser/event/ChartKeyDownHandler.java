package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Event;

/**
 * Handles the event where the user depresses a key.
 *
 * @author Chad Takahashi
 */
public class ChartKeyDownHandler extends AbstractEventHandler<KeyDownHandler>
    implements KeyDownHandler {


  public void onKeyDown(KeyDownEvent event) {
    ChartState chartInfo = getChartState(event);
    boolean handled = true;
    int keyCode = event.getNativeKeyCode();
    // all checks (except TAB) are to prevent focus stealing by the browser.
    if (keyCode == KeyCodes.KEY_PAGEUP
        || keyCode == KeyCodes.KEY_PAGEDOWN
        || keyCode == KeyCodes.KEY_UP
        || keyCode == KeyCodes.KEY_DOWN || keyCode == KeyCodes.KEY_TAB) {
      handled = handleTabKey((Event) event.getNativeEvent(), chartInfo, keyCode,
          event.isShiftKeyDown());
    }
    chartInfo.setHandled(handled);
    if (handled) {
       event.stopPropagation();
       event.preventDefault();
    }
  }
}
