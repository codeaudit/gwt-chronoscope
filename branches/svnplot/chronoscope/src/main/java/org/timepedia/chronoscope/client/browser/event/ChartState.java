/**
 * 
 */
package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;

/**
 * @author Chad Takahashi
 */
public class ChartState {

  public Chart chart;

  private CompoundUIAction compoundUIAction = new CompoundUIAction();

  private boolean isHandled;

  private int localX, localY;

  private int tabKeyEventCode = Event.ONKEYDOWN;

  /**
   * Describes the compound UI action currently taking place (if any).
   * A compound UI action is either a drag or select/highlight operation.
   */
  public CompoundUIAction getCompoundUIAction() {
    return compoundUIAction;
  }

  public int getLocalX() {
    return localX;
  }

  public int getLocalY() {
    return localY;
  }

  /**
   * Determines the browser-specific GWT event code associated with the tab key.
   */
  public int getTabKeyEventCode() {
    return tabKeyEventCode;
  }

  public boolean isHandled() {
    return isHandled;
  }

  public void setHandled(boolean handled) {
    this.isHandled = handled;
  }

  public void setLocalX(int localX) {
    this.localX = localX;
  }

  public void setLocalY(int localY) {
    this.localY = localY;
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
    return "localX=" + localX + "; localY=" + localY + "; tabKeyEventCode="
        + tabKeyEventCode + "; compoundUIAction=" + compoundUIAction;
  }

}
