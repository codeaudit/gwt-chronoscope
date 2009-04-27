/**
 *
 */
package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;
import com.google.gwt.event.shared.HandlerManager;

import org.timepedia.chronoscope.client.Chart;

/**
 * @author Chad Takahashi
 */
public class ChartState {

  public Chart chart;

  private CompoundUIAction compoundUIAction = new CompoundUIAction();

  private boolean isHandled;

  private int clientX, clientY;

  private int tabKeyEventCode = Event.ONKEYDOWN;

  private int originX;

  private int originY;

  private HandlerManager handlerLookup;

  public int getClientX() {
    return clientX;
  }

  public int getClientY() {
    return clientY;
  }

  /**
   * Describes the compound UI action currently taking place (if any). A
   * compound UI action is either a drag or select/highlight operation.
   */
  public CompoundUIAction getCompoundUIAction() {
    return compoundUIAction;
  }

  public int getLocalX() {
    return getClientX() - getOriginX();
  }

  public int getLocalY() {
    return getClientY() - getOriginY();
  }

  public int getOriginX() {
    return originX;
  }

  public int getOriginY() {
    return originY;
  }

  /**
   * Determines the browser-specific GWT event code associated with the tab
   * key.
   */
  public int getTabKeyEventCode() {
    return tabKeyEventCode;
  }

  public boolean isHandled() {
    return isHandled;
  }

  public void setClientX(int localX) {
    this.clientX = localX;
  }

  public void setClientY(int localY) {
    this.clientY = localY;
  }

  public void setHandled(boolean handled) {
    this.isHandled = handled;
  }

  public void setOriginX(int originX) {
    this.originX = originX;
  }

  public void setOriginY(int originY) {
    this.originY = originY;
  }

  /**
   * Gets reassigned based on browser type.
   *
   * @see #getTabKeyEventCode()
   */
  public void setTabKeyEventCode(int tabKeyEventCode) {
    this.tabKeyEventCode = tabKeyEventCode;
  }

  public String toString() {
    return "clientX=" + clientX + "; clientY=" + clientY + "; tabKeyEventCode="
        + tabKeyEventCode + "; compoundUIAction=" + compoundUIAction;
  }

  public HandlerManager getHandlerManager() {
    return handlerLookup;
  }

  public void setHandlerManager(HandlerManager handlerLookup) {

    this.handlerLookup = handlerLookup;
  }
}
