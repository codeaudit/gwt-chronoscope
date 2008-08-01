package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

/**
 * Handles discrete events related to the client (e.g. mouse clicks, key
 * presses).
 * 
 * @author Chad Takahashi
 */
public interface ClientEventHandler {

  /**
   * Handles a discrete client event by updating the specified
   * {@link ChartState} object.
   * 
   * @return true if the event was successfully handled.
   */
  boolean handle(Event event, int x, int y, ChartState chartInfo);

}
