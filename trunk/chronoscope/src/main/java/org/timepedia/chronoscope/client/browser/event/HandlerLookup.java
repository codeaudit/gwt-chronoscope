package org.timepedia.chronoscope.client.browser.event;

import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;

/**
 * Looks up the handler object associated to a given event key.
 * 
 * @author Chad Takahashi
 */
public final class HandlerLookup {
  
  /**
   * Associates an <tt>int</tt> event key with a Handler value.
   */
  private static final class HandlerEntry {
    public ClientEventHandler handler;

    public int key;
    public HandlerEntry(int key, ClientEventHandler handler) {
      this.key = key;
      this.handler = handler;
    }
  }

  private ArrayList<HandlerEntry> handlers = new ArrayList<HandlerEntry>();

  /**
   * Adds a {@link ClientEventHandler} object to this lookup container.
   */
  public void addHandler(int eventKey, ClientEventHandler handler) {
    ArgChecker.isNotNull(handler, "handler");
    handlers.add(new HandlerEntry(eventKey, handler));
  }

  /**
   * Returns the handler associated with the specified eventKey, or null if none
   * exist.
   */
  public ClientEventHandler getHandler(int eventKey) {
    ClientEventHandler handler = null;

    for (HandlerEntry entry : handlers) {
      if (entry.key == eventKey) {
        return entry.handler;
      }
    }
    return handler;
  }
}
