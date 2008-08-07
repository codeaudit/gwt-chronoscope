package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.libideas.event.client.KeyDownEvent;
import com.google.gwt.libideas.event.client.KeyDownHandler;
import com.google.gwt.user.client.ui.KeyboardListener;

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
    int keyCode = event.getKeyCode();
    // all checks (except TAB) are to prevent focus stealing by the browser.
    if (keyCode == KeyboardListener.KEY_PAGEUP
        || keyCode == KeyboardListener.KEY_PAGEDOWN
        || keyCode == KeyboardListener.KEY_UP
        || keyCode == KeyboardListener.KEY_DOWN 
        || keyCode == KEY_TAB) {
      handled = handleTabKey(event.getBrowserEvent(), chartInfo, keyCode, event.isShiftKeyDown());
    }
    chartInfo.setHandled(true);
  }


}
