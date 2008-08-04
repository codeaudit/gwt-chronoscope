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

  // TODO: should these fields be defined in the Chart class?
  public boolean selActive = false;

  public boolean maybeDrag = false;

  public int selStart = -1, dragStart = -1;

  // Gets reassigned based on browser type
  public int tabKeyEventCode = Event.ONKEYDOWN;

  private boolean handled;

  private int localX;

  private int localY;

  public int getLocalX() {
    return localX;
  }

  public int getLocalY() {
    return localY;
  }

  public boolean isHandled() {
    return handled;
  }

  public void setHandled(boolean handled) {
    this.handled = handled;
  }

  public void setLocalX(int localX) {
    this.localX = localX;
  }

  public void setLocalY(int localY) {
    this.localY = localY;
  }
}
