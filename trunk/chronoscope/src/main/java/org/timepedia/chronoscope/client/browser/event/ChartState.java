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
}
