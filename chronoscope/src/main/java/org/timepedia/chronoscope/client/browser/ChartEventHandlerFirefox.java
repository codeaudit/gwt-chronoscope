package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Event;

/**
 *
 */
public class ChartEventHandlerFirefox extends ChartEventHandler {

  public int getTabKeyEventType() {
    return Event.ONKEYPRESS;
  }
}
